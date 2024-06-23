package emsi.soukant.projetangular;

import emsi.soukant.projetangular.dtos.BankAccountDTO;
import emsi.soukant.projetangular.dtos.CurrentBankAccountDTO;
import emsi.soukant.projetangular.dtos.CustomerDTO;
import emsi.soukant.projetangular.dtos.SavingBankAccountDTO;
import emsi.soukant.projetangular.entities.AccountOperation;
import emsi.soukant.projetangular.entities.CurrentAccount;
import emsi.soukant.projetangular.entities.Customer;
import emsi.soukant.projetangular.entities.SavingAccount;
import emsi.soukant.projetangular.enums.AccountStatus;
import emsi.soukant.projetangular.enums.OperationType;
import emsi.soukant.projetangular.exceptions.CustomerNotFoundException;
import emsi.soukant.projetangular.repositories.AccountOperationRepository;
import emsi.soukant.projetangular.repositories.BankAccountRepository;
import emsi.soukant.projetangular.repositories.CustomerRepository;
import emsi.soukant.projetangular.services.BankAccountService;
import emsi.soukant.projetangular.entities.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@SpringBootApplication
public class ProjetAngularApplication {

    public static void main(String[] args) {

        SpringApplication.run(ProjetAngularApplication.class, args);
    }
    @Bean
    CommandLineRunner commandLineRunner(BankAccountService bankAccountService){
        return args -> {
            Stream.of("Ismail","Yassin","Aymen").forEach(name->{
                CustomerDTO customer=new CustomerDTO();
                customer.setName(name);
                customer.setEmail(name+"@gmail.com");
                bankAccountService.saveCustomer(customer);
            });
            bankAccountService.listCustomer().forEach(customer -> {
                try {
                    bankAccountService.saveCurrentBankAccount(Math.random()*90000,9000, customer.getId());
                    bankAccountService.saveSavingBankAccount(Math.random()*120000,5.5, customer.getId());

                } catch (CustomerNotFoundException e) {
                    e.printStackTrace();
                }
            });
            List<BankAccountDTO> bankAccounts=bankAccountService.bankAccountList();
            for (BankAccountDTO bankAccount:bankAccounts){
                for (int i = 0; i < 10; i++) {
                    String accountId;
                    if(bankAccount instanceof SavingBankAccountDTO)
                        accountId=((SavingBankAccountDTO)bankAccount).getId();
                    else
                        accountId=((CurrentBankAccountDTO)bankAccount).getId();
                    bankAccountService.credit(accountId, 10+Math.random()*12,"Credit");
                    bankAccountService.debit(accountId,10+Math.random()*9,"Debit");
                }
            }
        };
    }
    //@Bean
    CommandLineRunner start(CustomerRepository customerRepository,
                            BankAccountRepository bankAccountRepository,
                            AccountOperationRepository accountOperationRepository){
        return args -> {
            Stream.of("Salim","Salman","Zakia").forEach(name->{
                Customer customer=new Customer();
                customer.setName(name);
                customer.setEmail(name+"@gmail.com");
                customerRepository.save(customer);
            });
            customerRepository.findAll().forEach(cust -> {
                CurrentAccount currentAccount=new CurrentAccount();
                currentAccount.setId(UUID.randomUUID().toString());
                currentAccount.setBalance(Math.random()*90000);
                currentAccount.setCreateAt(new Date());
                currentAccount.setStatus(AccountStatus.CREATED);
                currentAccount.setCustomer(cust);
                currentAccount.setOverDraft(9000);
                bankAccountRepository.save(currentAccount);

                SavingAccount savingAccount=new SavingAccount();
                savingAccount.setId(UUID.randomUUID().toString());
                savingAccount.setBalance(Math.random()*90000);
                savingAccount.setCreateAt(new Date());
                savingAccount.setStatus(AccountStatus.CREATED);
                savingAccount.setCustomer(cust);
                savingAccount.setInterestRate(5.5);
                bankAccountRepository.save(savingAccount);
            });
            bankAccountRepository.findAll().forEach(acc->{
                for (int i = 0; i < 10; i++) {
                    AccountOperation accountOperation=new AccountOperation();
                    accountOperation.setOperationDate(new Date());
                    accountOperation.setAmount(Math.random()*12000);
                    accountOperation.setType(Math.random()>0.5? OperationType.DEBIT: OperationType.CREDIT);
                    accountOperation.setBankAccount(acc);
                    accountOperationRepository.save(accountOperation);
                }


            });



        };
    }

}
