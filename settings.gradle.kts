rootProject.name = "BankingSystem"

include("bank-service")
include("bank-service:domain")
include("bank-service:application")
include("bank-service:infrastructure")
include("bank-service:presentation")

include("rates-service")
include("messaging-contracts")