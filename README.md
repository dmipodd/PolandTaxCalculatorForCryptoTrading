# Crypto Tax Calculator for Poland
## 📘 About
This application calculates Poland tax resident taxes for cryptocurrency trading, following local tax regulations. 
It processes transaction data and provides an accurate tax report.

⚠️ Please, note, that the application doesn't take into consideration your crypto withdrawals. 
If you withdrew some crypto to pay for goods or services in crypto - 
you are obliged to consider this as a SELL transaction and reflect in tax report accordingly!  

Read more about the app: https://medium.com/@dzmpdd/my-pet-project-a-poland-tax-calculator-for-crypto-trading-70d9dc3e1537 

## 🛠️ How to run the app
1) put your input files (NBP rates and a file with transations) to 
   _{project}\src\main\resources_

   and update the configuration accordingly in 

   _{project}\src\main\resources\config.yaml_
2) build the project with maven 3+ and java 17+:

   _mvn clean package_

3) run the app

   _java -jar target/polandTaxCalculatorForCryptoTrading-1.0.jar_

4) if there were no errors then it means that the output file was generated. 
   Find the output file with the tax report and use it to fill in PIT-38, section E tax declaration. 