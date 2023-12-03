package lv.gstg.javademo.transactions.core;

import lv.gstg.javademo.transactions.core.dto.AccountDetails;
import lv.gstg.javademo.transactions.model.Account;
import org.mapstruct.Mapper;

@Mapper
interface AccountMapper {
    AccountDetails toDetails(Account account);
}
