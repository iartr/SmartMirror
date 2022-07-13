dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Smart Mirror"
include(":app")

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
include(":features:news")
include(":features:currency")
include(":features:weather")
include(":features:coordinates")
include(":features:coordinates:api")
include(":features:coordinates:impl")
include(":camera")
