package filters;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.util.SubnetUtils;
import org.slf4j.Logger;

import com.google.inject.Inject;

import ninja.Context;
import ninja.Filter;
import ninja.FilterChain;
import ninja.Result;
import ninja.Results;
import ninja.utils.NinjaProperties;

public class IPAddressFilter implements Filter {

    @Inject
    Logger logger;

    @Inject
    NinjaProperties ninjaProperties;

    @Override
    public Result filter(FilterChain filterChain, Context context) {
        if (StringUtils.isBlank(ninjaProperties.get("mail-baku.allowed.addresses"))) {
            return filterChain.next(context);
        }

        String remoteIpAddress = context.getHeader("X-Forwarded-For");
        if (StringUtils.isNotBlank(remoteIpAddress)) {
            remoteIpAddress = StringUtils.substring(remoteIpAddress, 0, StringUtils.indexOf(remoteIpAddress, ":"));
        } else {
            remoteIpAddress = context.getRemoteAddr();
        }

        String[] allowedAddresses = StringUtils.split(ninjaProperties.get("mail-baku.allowed.addresses"), ',');
        for (String address : allowedAddresses) {
            SubnetUtils allowedSubnet = createSubnetUtils(address);
            if (allowed(remoteIpAddress, allowedSubnet)) {
                return filterChain.next(context);
            }
        }

        logger.error("IP address not allowed. ip = " + remoteIpAddress);
        return Results.forbidden().text().render("IP address not allowed.");
    }

    protected boolean allowed(String remoteIpAddress, SubnetUtils subnet) {
        if (subnet == null) {
            return true;
        }
        try {
            subnet.setInclusiveHostCount(true);
            return subnet.getInfo().isInRange(remoteIpAddress);
        } catch (IllegalArgumentException e) {
            logger.error("Failed to parse ip address", e);
        }
        return false;
    }

    protected SubnetUtils createSubnetUtils(String remoteIpAddress) {
        if (StringUtils.isNotBlank(remoteIpAddress)) {
            if (!StringUtils.contains(remoteIpAddress, "/")) {
                remoteIpAddress += "/32";
            }
            return new SubnetUtils(remoteIpAddress);
        }
        return null;
    }

}
