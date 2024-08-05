rootProject.name = "cloud"
include("networking")
include("driver")
include("api")
include("launcher")

include("runnable")
include("runnable:manager")
findProject(":runnable:manager")?.name = "manager"
include("plugin")
