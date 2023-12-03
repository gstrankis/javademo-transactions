package lv.gstg.javademo.transactions.core;

import lv.gstg.javademo.transactions.core.dto.TransactionDetails;
import lv.gstg.javademo.transactions.model.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.ZoneOffset;

@Mapper(imports = {ZoneOffset.class})
interface TransactionMapper {
    @Mapping(target = "createdAt", expression = "java(transaction.getCreatedAt().atZone(ZoneOffset.UTC))")
    @Mapping(target = "accountId", source = "transaction.account.id")
    @Mapping(target = "transferId", source = "transaction.transfer.id")
    TransactionDetails toDetails(Transaction transaction);
}
