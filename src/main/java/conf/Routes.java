
package conf;

import controllers.InboxController;
import controllers.ServerController;
import ninja.AssetsController;
import ninja.Router;
import ninja.application.ApplicationRoutes;

public class Routes implements ApplicationRoutes {

    @Override
    public void init(Router router) {

        router.GET().route("/inbox").with(InboxController::index);
        router.GET().route("/inbox/{id}").with(InboxController::show);
        router.GET().route("/inbox/{id}/attachment/{index}/{filename}").with(InboxController::attachment);
        router.GET().route("/inbox/{id}/raw").with(InboxController::raw);

        router.GET().route("/server").with(ServerController::index);

        ///////////////////////////////////////////////////////////////////////
        // Assets (pictures / javascript)
        ///////////////////////////////////////////////////////////////////////
        router.GET().route("/assets/webjars/{fileName: .*}").with(AssetsController::serveWebJars);
        router.GET().route("/assets/{fileName: .*}").with(AssetsController::serveStatic);

        ///////////////////////////////////////////////////////////////////////
        // Index / Catchall shows index page
        ///////////////////////////////////////////////////////////////////////
        router.GET().route("/.*").with(InboxController::index);
    }

}
