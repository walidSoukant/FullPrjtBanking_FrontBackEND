package emsi.soukant.projetangular.repositories;

import emsi.soukant.projetangular.entities.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankAccountRepository extends JpaRepository<BankAccount,String > {

}
