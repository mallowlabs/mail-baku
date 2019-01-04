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

        String[] allowedAddresses = StringUtils.split(ninjaProperties.get("mail-baku.allowed.addresses"), ',');
        for (String address : allowedAddresses) {
            SubnetUtils allowedSubnet = createSubnetUtils(address);
            if (allowed(context.getRemoteAddr(), allowedSubnet)) {
                return filterChain.next(context);
            }
        }

        return Results.forbidden().text().render("IP address not allowed.");
    }

    protected boolean allowed(String remoteIpAddress, SubnetUtils subnet) {
        if (subnet == null) {
            return true;
        }
        try {
            subnet.setInclusiveHostCount(true); // xx.xx.xx.xx/32 に対応するため
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
