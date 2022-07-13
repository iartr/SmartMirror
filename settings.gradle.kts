dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Smart Mirror"

include(":app")

include(":camera")

include(":core")
include(":core:utils")
include(":core:design")
include(":core:toggles-api")
include(":core:account-api")
include(":core:mvvm")
include(":core:ext")
include(":core:network")

include(":features")
include(":features:settings")
include(":features:settings:api")
include(":features:settings:impl")
include(":features:news")
include(":features:news:api")
include(":features:news:impl")
include(":features:currency")
include(":features:currency:api")
include(":features:currency:impl")
include(":features:weather")
include(":features:weather:api")
include(":features:weather:impl")
include(":features:coordinates")
include(":features:coordinates:api")
include(":features:coordinates:impl")
include(":features:mirror")
