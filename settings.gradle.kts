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

include(":features")
include(":features:settings")
include(":core:network")
include(":features:news")
include(":features:currency")
