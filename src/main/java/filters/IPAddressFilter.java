package filters;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.util.SubnetUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.server.ServerRequestFilter;

import io.vertx.ext.web.RoutingContext;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public class IPAddressFilter {

    private static final Logger logger = Logger.getLogger(IPAddressFilter.class);

    @ConfigProperty(name = "mail-baku.allowed.addresses")
    Optional<String> allowedAddresses;

    @ServerRequestFilter
    public Optional<Response> filter(ContainerRequestContext context, RoutingContext routingContext) {
        if (StringUtils.isBlank(allowedAddresses.orElse(null))) {
            return Optional.empty();
        }

        String remoteIpAddress = context.getHeaderString("X-Forwarded-For");
        if (StringUtils.isNotBlank(remoteIpAddress)) {
            remoteIpAddress = StringUtils.substring(remoteIpAddress, 0, StringUtils.indexOf(remoteIpAddress, ":"));
        } else {
            remoteIpAddress = routingContext.request().remoteAddress().hostAddress();
        }

        String[] allowedList = StringUtils.split(allowedAddresses.get(), ',');
        for (String address : allowedList) {
            SubnetUtils allowedSubnet = createSubnetUtils(address);
            if (allowed(remoteIpAddress, allowedSubnet)) {
                return Optional.empty();
            }
        }

        logger.error("IP address not allowed. ip = " + remoteIpAddress);
        return Optional.of(Response.status(Response.Status.FORBIDDEN)
                .type(MediaType.TEXT_PLAIN)
                .entity("IP address not allowed.")
                .build());
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
