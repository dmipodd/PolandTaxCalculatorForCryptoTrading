# Crypto Tax Calculator for Poland
This application calculates Poland tax resident taxes for cryptocurrency trading, following local tax regulations. 
It processes transaction data and provides an accurate tax report.

Read more about the app: https://medium.com/@dzmpdd/my-pet-project-a-poland-tax-calculator-for-crypto-trading-70d9dc3e1537 

# How to run the app
1) put your input files (NBP rates and a file with Bitstamp transations) to _{project}\src\main\resources_

   and update configuration in _{project}\src\main\resources\config.yaml_ accordingly
3) build the project with maven 3+ and java 17+:

   _mvn clean package_

3) run the app

   _java -jar target/polandTaxCalculatorForCryptoTrading-1.0.jar_
