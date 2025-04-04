package com.rabbit.blockchain.wrapper;

import io.reactivex.Flowable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.DynamicBytes;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.StaticStruct;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/hyperledger-web3j/web3j/tree/main/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 1.6.3.
 */
@SuppressWarnings("rawtypes")
public class RepaymentScheduler extends Contract {
    public static final String BINARY = "Bin file was not provided";

    public static final String FUNC_MAX_OVERDUE_INTEREST_RATE = "MAX_OVERDUE_INTEREST_RATE";

    public static final String FUNC_ACTIVEREPAYMENTS = "activeRepayments";

    public static final String FUNC_CHECKUPKEEP = "checkUpkeep";

    public static final String FUNC_CLEANUPREPAYMENTDATA = "cleanupRepaymentData";

    public static final String FUNC_GETACTIVEREPAYMENTS = "getActiveRepayments";

    public static final String FUNC_GETEARLYREPAYMENTFEE = "getEarlyRepaymentFee";

    public static final String FUNC_GETREPAYMENTINFO = "getRepaymentInfo";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_PERFORMUPKEEP = "performUpkeep";

    public static final String FUNC_PROCESSEARLYREPAYMENT = "processEarlyRepayment";

    public static final String FUNC_PROCESSREPAYMENT = "processRepayment";

    public static final String FUNC_PROMISSORYNOTEADDRESS = "promissoryNoteAddress";

    public static final String FUNC_RABBITCOINADDRESS = "rabbitCoinAddress";

    public static final String FUNC_REGISTERREPAYMENTSCHEDULE = "registerRepaymentSchedule";

    public static final String FUNC_RENOUNCEOWNERSHIP = "renounceOwnership";

    public static final String FUNC_REPAYMENTSCHEDULES = "repaymentSchedules";

    public static final String FUNC_TRANSFEROWNERSHIP = "transferOwnership";

    public static final String FUNC_UPDATECONTRACTADDRESSES = "updateContractAddresses";

    public static final String FUNC_UPDATEOVERDUEINFO = "updateOverdueInfo";

    public static final String FUNC_UPDATEREPAYMENTINFO = "updateRepaymentInfo";

    public static final Event ACCELREACHED_EVENT = new Event("AccelReached", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>(true) {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event BURNFAILED_EVENT = new Event("BurnFailed", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Utf8String>() {}));
    ;

    public static final Event EARLYREPAYMENTFEE_EVENT = new Event("EarlyRepaymentFee", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>(true) {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event EARLYREPAYMENTPRINCIPAL_EVENT = new Event("EarlyRepaymentPrincipal", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>(true) {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Bool>() {}));
    ;

    public static final Event INSUFFICIENTBALANCE_EVENT = new Event("InsufficientBalance", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Address>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event OVERDUEINFOUPDATED_EVENT = new Event("OverdueInfoUpdated", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>(true) {}, new TypeReference<Bool>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event OVERDUEINTERESTACCUMULATED_EVENT = new Event("OverdueInterestAccumulated", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>(true) {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event OVERDUERESOLVED_EVENT = new Event("OverdueResolved", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>(true) {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event OWNERSHIPTRANSFERRED_EVENT = new Event("OwnershipTransferred", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}));
    ;

    public static final Event REPAYMENTCOMPLETED_EVENT = new Event("RepaymentCompleted", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;

    public static final Event REPAYMENTOVERDUE_EVENT = new Event("RepaymentOverdue", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>(true) {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event REPAYMENTPROCESSED_EVENT = new Event("RepaymentProcessed", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event REPAYMENTSCHEDULECREATED_EVENT = new Event("RepaymentScheduleCreated", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
    ;

    @Deprecated
    protected RepaymentScheduler(String contractAddress, Web3j web3j, Credentials credentials,
            BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected RepaymentScheduler(String contractAddress, Web3j web3j, Credentials credentials,
            ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected RepaymentScheduler(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected RepaymentScheduler(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static List<AccelReachedEventResponse> getAccelReachedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(ACCELREACHED_EVENT, transactionReceipt);
        ArrayList<AccelReachedEventResponse> responses = new ArrayList<AccelReachedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            AccelReachedEventResponse typedResponse = new AccelReachedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.tokenId = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.maxInterestRate = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static AccelReachedEventResponse getAccelReachedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(ACCELREACHED_EVENT, log);
        AccelReachedEventResponse typedResponse = new AccelReachedEventResponse();
        typedResponse.log = log;
        typedResponse.tokenId = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.maxInterestRate = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public Flowable<AccelReachedEventResponse> accelReachedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getAccelReachedEventFromLog(log));
    }

    public Flowable<AccelReachedEventResponse> accelReachedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(ACCELREACHED_EVENT));
        return accelReachedEventFlowable(filter);
    }

    public static List<BurnFailedEventResponse> getBurnFailedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(BURNFAILED_EVENT, transactionReceipt);
        ArrayList<BurnFailedEventResponse> responses = new ArrayList<BurnFailedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            BurnFailedEventResponse typedResponse = new BurnFailedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.tokenId = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.reason = (String) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static BurnFailedEventResponse getBurnFailedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(BURNFAILED_EVENT, log);
        BurnFailedEventResponse typedResponse = new BurnFailedEventResponse();
        typedResponse.log = log;
        typedResponse.tokenId = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.reason = (String) eventValues.getNonIndexedValues().get(1).getValue();
        return typedResponse;
    }

    public Flowable<BurnFailedEventResponse> burnFailedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getBurnFailedEventFromLog(log));
    }

    public Flowable<BurnFailedEventResponse> burnFailedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(BURNFAILED_EVENT));
        return burnFailedEventFlowable(filter);
    }

    public static List<EarlyRepaymentFeeEventResponse> getEarlyRepaymentFeeEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(EARLYREPAYMENTFEE_EVENT, transactionReceipt);
        ArrayList<EarlyRepaymentFeeEventResponse> responses = new ArrayList<EarlyRepaymentFeeEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            EarlyRepaymentFeeEventResponse typedResponse = new EarlyRepaymentFeeEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.tokenId = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.feeAmount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static EarlyRepaymentFeeEventResponse getEarlyRepaymentFeeEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(EARLYREPAYMENTFEE_EVENT, log);
        EarlyRepaymentFeeEventResponse typedResponse = new EarlyRepaymentFeeEventResponse();
        typedResponse.log = log;
        typedResponse.tokenId = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.feeAmount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public Flowable<EarlyRepaymentFeeEventResponse> earlyRepaymentFeeEventFlowable(
            EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getEarlyRepaymentFeeEventFromLog(log));
    }

    public Flowable<EarlyRepaymentFeeEventResponse> earlyRepaymentFeeEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(EARLYREPAYMENTFEE_EVENT));
        return earlyRepaymentFeeEventFlowable(filter);
    }

    public static List<EarlyRepaymentPrincipalEventResponse> getEarlyRepaymentPrincipalEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(EARLYREPAYMENTPRINCIPAL_EVENT, transactionReceipt);
        ArrayList<EarlyRepaymentPrincipalEventResponse> responses = new ArrayList<EarlyRepaymentPrincipalEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            EarlyRepaymentPrincipalEventResponse typedResponse = new EarlyRepaymentPrincipalEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.tokenId = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.principalAmount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.remainingPrincipal = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.isFullRepayment = (Boolean) eventValues.getNonIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static EarlyRepaymentPrincipalEventResponse getEarlyRepaymentPrincipalEventFromLog(
            Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(EARLYREPAYMENTPRINCIPAL_EVENT, log);
        EarlyRepaymentPrincipalEventResponse typedResponse = new EarlyRepaymentPrincipalEventResponse();
        typedResponse.log = log;
        typedResponse.tokenId = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.principalAmount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.remainingPrincipal = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
        typedResponse.isFullRepayment = (Boolean) eventValues.getNonIndexedValues().get(2).getValue();
        return typedResponse;
    }

    public Flowable<EarlyRepaymentPrincipalEventResponse> earlyRepaymentPrincipalEventFlowable(
            EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getEarlyRepaymentPrincipalEventFromLog(log));
    }

    public Flowable<EarlyRepaymentPrincipalEventResponse> earlyRepaymentPrincipalEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(EARLYREPAYMENTPRINCIPAL_EVENT));
        return earlyRepaymentPrincipalEventFlowable(filter);
    }

    public static List<InsufficientBalanceEventResponse> getInsufficientBalanceEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(INSUFFICIENTBALANCE_EVENT, transactionReceipt);
        ArrayList<InsufficientBalanceEventResponse> responses = new ArrayList<InsufficientBalanceEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            InsufficientBalanceEventResponse typedResponse = new InsufficientBalanceEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.tokenId = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.debtor = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.requiredAmount = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.currentBalance = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static InsufficientBalanceEventResponse getInsufficientBalanceEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(INSUFFICIENTBALANCE_EVENT, log);
        InsufficientBalanceEventResponse typedResponse = new InsufficientBalanceEventResponse();
        typedResponse.log = log;
        typedResponse.tokenId = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.debtor = (String) eventValues.getNonIndexedValues().get(1).getValue();
        typedResponse.requiredAmount = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
        typedResponse.currentBalance = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
        return typedResponse;
    }

    public Flowable<InsufficientBalanceEventResponse> insufficientBalanceEventFlowable(
            EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getInsufficientBalanceEventFromLog(log));
    }

    public Flowable<InsufficientBalanceEventResponse> insufficientBalanceEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(INSUFFICIENTBALANCE_EVENT));
        return insufficientBalanceEventFlowable(filter);
    }

    public static List<OverdueInfoUpdatedEventResponse> getOverdueInfoUpdatedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(OVERDUEINFOUPDATED_EVENT, transactionReceipt);
        ArrayList<OverdueInfoUpdatedEventResponse> responses = new ArrayList<OverdueInfoUpdatedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            OverdueInfoUpdatedEventResponse typedResponse = new OverdueInfoUpdatedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.tokenId = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.isOverdue = (Boolean) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.accumulatedOverdueInterest = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static OverdueInfoUpdatedEventResponse getOverdueInfoUpdatedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(OVERDUEINFOUPDATED_EVENT, log);
        OverdueInfoUpdatedEventResponse typedResponse = new OverdueInfoUpdatedEventResponse();
        typedResponse.log = log;
        typedResponse.tokenId = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.isOverdue = (Boolean) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.accumulatedOverdueInterest = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
        return typedResponse;
    }

    public Flowable<OverdueInfoUpdatedEventResponse> overdueInfoUpdatedEventFlowable(
            EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getOverdueInfoUpdatedEventFromLog(log));
    }

    public Flowable<OverdueInfoUpdatedEventResponse> overdueInfoUpdatedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(OVERDUEINFOUPDATED_EVENT));
        return overdueInfoUpdatedEventFlowable(filter);
    }

    public static List<OverdueInterestAccumulatedEventResponse> getOverdueInterestAccumulatedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(OVERDUEINTERESTACCUMULATED_EVENT, transactionReceipt);
        ArrayList<OverdueInterestAccumulatedEventResponse> responses = new ArrayList<OverdueInterestAccumulatedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            OverdueInterestAccumulatedEventResponse typedResponse = new OverdueInterestAccumulatedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.tokenId = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.newInterest = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.totalAccumulatedInterest = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static OverdueInterestAccumulatedEventResponse getOverdueInterestAccumulatedEventFromLog(
            Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(OVERDUEINTERESTACCUMULATED_EVENT, log);
        OverdueInterestAccumulatedEventResponse typedResponse = new OverdueInterestAccumulatedEventResponse();
        typedResponse.log = log;
        typedResponse.tokenId = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.newInterest = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.totalAccumulatedInterest = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
        return typedResponse;
    }

    public Flowable<OverdueInterestAccumulatedEventResponse> overdueInterestAccumulatedEventFlowable(
            EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getOverdueInterestAccumulatedEventFromLog(log));
    }

    public Flowable<OverdueInterestAccumulatedEventResponse> overdueInterestAccumulatedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(OVERDUEINTERESTACCUMULATED_EVENT));
        return overdueInterestAccumulatedEventFlowable(filter);
    }

    public static List<OverdueResolvedEventResponse> getOverdueResolvedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(OVERDUERESOLVED_EVENT, transactionReceipt);
        ArrayList<OverdueResolvedEventResponse> responses = new ArrayList<OverdueResolvedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            OverdueResolvedEventResponse typedResponse = new OverdueResolvedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.tokenId = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.paidOverdueAmount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static OverdueResolvedEventResponse getOverdueResolvedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(OVERDUERESOLVED_EVENT, log);
        OverdueResolvedEventResponse typedResponse = new OverdueResolvedEventResponse();
        typedResponse.log = log;
        typedResponse.tokenId = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.paidOverdueAmount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public Flowable<OverdueResolvedEventResponse> overdueResolvedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getOverdueResolvedEventFromLog(log));
    }

    public Flowable<OverdueResolvedEventResponse> overdueResolvedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(OVERDUERESOLVED_EVENT));
        return overdueResolvedEventFlowable(filter);
    }

    public static List<OwnershipTransferredEventResponse> getOwnershipTransferredEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(OWNERSHIPTRANSFERRED_EVENT, transactionReceipt);
        ArrayList<OwnershipTransferredEventResponse> responses = new ArrayList<OwnershipTransferredEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            OwnershipTransferredEventResponse typedResponse = new OwnershipTransferredEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.previousOwner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.newOwner = (String) eventValues.getIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static OwnershipTransferredEventResponse getOwnershipTransferredEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(OWNERSHIPTRANSFERRED_EVENT, log);
        OwnershipTransferredEventResponse typedResponse = new OwnershipTransferredEventResponse();
        typedResponse.log = log;
        typedResponse.previousOwner = (String) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.newOwner = (String) eventValues.getIndexedValues().get(1).getValue();
        return typedResponse;
    }

    public Flowable<OwnershipTransferredEventResponse> ownershipTransferredEventFlowable(
            EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getOwnershipTransferredEventFromLog(log));
    }

    public Flowable<OwnershipTransferredEventResponse> ownershipTransferredEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(OWNERSHIPTRANSFERRED_EVENT));
        return ownershipTransferredEventFlowable(filter);
    }

    public static List<RepaymentCompletedEventResponse> getRepaymentCompletedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(REPAYMENTCOMPLETED_EVENT, transactionReceipt);
        ArrayList<RepaymentCompletedEventResponse> responses = new ArrayList<RepaymentCompletedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            RepaymentCompletedEventResponse typedResponse = new RepaymentCompletedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.tokenId = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static RepaymentCompletedEventResponse getRepaymentCompletedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(REPAYMENTCOMPLETED_EVENT, log);
        RepaymentCompletedEventResponse typedResponse = new RepaymentCompletedEventResponse();
        typedResponse.log = log;
        typedResponse.tokenId = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public Flowable<RepaymentCompletedEventResponse> repaymentCompletedEventFlowable(
            EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getRepaymentCompletedEventFromLog(log));
    }

    public Flowable<RepaymentCompletedEventResponse> repaymentCompletedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(REPAYMENTCOMPLETED_EVENT));
        return repaymentCompletedEventFlowable(filter);
    }

    public static List<RepaymentOverdueEventResponse> getRepaymentOverdueEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(REPAYMENTOVERDUE_EVENT, transactionReceipt);
        ArrayList<RepaymentOverdueEventResponse> responses = new ArrayList<RepaymentOverdueEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            RepaymentOverdueEventResponse typedResponse = new RepaymentOverdueEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.tokenId = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.overdueStartDate = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.totalDefaultCount = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static RepaymentOverdueEventResponse getRepaymentOverdueEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(REPAYMENTOVERDUE_EVENT, log);
        RepaymentOverdueEventResponse typedResponse = new RepaymentOverdueEventResponse();
        typedResponse.log = log;
        typedResponse.tokenId = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.overdueStartDate = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.totalDefaultCount = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
        return typedResponse;
    }

    public Flowable<RepaymentOverdueEventResponse> repaymentOverdueEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getRepaymentOverdueEventFromLog(log));
    }

    public Flowable<RepaymentOverdueEventResponse> repaymentOverdueEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(REPAYMENTOVERDUE_EVENT));
        return repaymentOverdueEventFlowable(filter);
    }

    public static List<RepaymentProcessedEventResponse> getRepaymentProcessedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(REPAYMENTPROCESSED_EVENT, transactionReceipt);
        ArrayList<RepaymentProcessedEventResponse> responses = new ArrayList<RepaymentProcessedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            RepaymentProcessedEventResponse typedResponse = new RepaymentProcessedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.tokenId = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.remainingPrincipal = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.nextMpDt = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static RepaymentProcessedEventResponse getRepaymentProcessedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(REPAYMENTPROCESSED_EVENT, log);
        RepaymentProcessedEventResponse typedResponse = new RepaymentProcessedEventResponse();
        typedResponse.log = log;
        typedResponse.tokenId = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
        typedResponse.remainingPrincipal = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
        typedResponse.nextMpDt = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
        return typedResponse;
    }

    public Flowable<RepaymentProcessedEventResponse> repaymentProcessedEventFlowable(
            EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getRepaymentProcessedEventFromLog(log));
    }

    public Flowable<RepaymentProcessedEventResponse> repaymentProcessedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(REPAYMENTPROCESSED_EVENT));
        return repaymentProcessedEventFlowable(filter);
    }

    public static List<RepaymentScheduleCreatedEventResponse> getRepaymentScheduleCreatedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(REPAYMENTSCHEDULECREATED_EVENT, transactionReceipt);
        ArrayList<RepaymentScheduleCreatedEventResponse> responses = new ArrayList<RepaymentScheduleCreatedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            RepaymentScheduleCreatedEventResponse typedResponse = new RepaymentScheduleCreatedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.tokenId = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.remainingPrincipal = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.nextMpDt = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static RepaymentScheduleCreatedEventResponse getRepaymentScheduleCreatedEventFromLog(
            Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(REPAYMENTSCHEDULECREATED_EVENT, log);
        RepaymentScheduleCreatedEventResponse typedResponse = new RepaymentScheduleCreatedEventResponse();
        typedResponse.log = log;
        typedResponse.tokenId = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.remainingPrincipal = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
        typedResponse.nextMpDt = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
        return typedResponse;
    }

    public Flowable<RepaymentScheduleCreatedEventResponse> repaymentScheduleCreatedEventFlowable(
            EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getRepaymentScheduleCreatedEventFromLog(log));
    }

    public Flowable<RepaymentScheduleCreatedEventResponse> repaymentScheduleCreatedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(REPAYMENTSCHEDULECREATED_EVENT));
        return repaymentScheduleCreatedEventFlowable(filter);
    }

    public RemoteFunctionCall<BigInteger> MAX_OVERDUE_INTEREST_RATE() {
        final Function function = new Function(FUNC_MAX_OVERDUE_INTEREST_RATE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> activeRepayments(BigInteger param0) {
        final Function function = new Function(FUNC_ACTIVEREPAYMENTS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<Tuple2<Boolean, byte[]>> checkUpkeep(byte[] param0) {
        final Function function = new Function(FUNC_CHECKUPKEEP, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicBytes(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}, new TypeReference<DynamicBytes>() {}));
        return new RemoteFunctionCall<Tuple2<Boolean, byte[]>>(function,
                new Callable<Tuple2<Boolean, byte[]>>() {
                    @Override
                    public Tuple2<Boolean, byte[]> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple2<Boolean, byte[]>(
                                (Boolean) results.get(0).getValue(), 
                                (byte[]) results.get(1).getValue());
                    }
                });
    }

    public RemoteFunctionCall<TransactionReceipt> cleanupRepaymentData(BigInteger tokenId) {
        final Function function = new Function(
                FUNC_CLEANUPREPAYMENTDATA, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(tokenId)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<List> getActiveRepayments() {
        final Function function = new Function(FUNC_GETACTIVEREPAYMENTS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Uint256>>() {}));
        return new RemoteFunctionCall<List>(function,
                new Callable<List>() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public List call() throws Exception {
                        List<Type> result = (List<Type>) executeCallSingleValueReturn(function, List.class);
                        return convertToNative(result);
                    }
                });
    }

    public RemoteFunctionCall<BigInteger> getEarlyRepaymentFee(BigInteger tokenId,
            BigInteger paymentAmount) {
        final Function function = new Function(FUNC_GETEARLYREPAYMENTFEE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(tokenId), 
                new org.web3j.abi.datatypes.generated.Uint256(paymentAmount)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<RepaymentInfo> getRepaymentInfo(BigInteger tokenId) {
        final Function function = new Function(FUNC_GETREPAYMENTINFO, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(tokenId)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<RepaymentInfo>() {}));
        return executeRemoteCallSingleValueReturn(function, RepaymentInfo.class);
    }

    public RemoteFunctionCall<String> owner() {
        final Function function = new Function(FUNC_OWNER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> performUpkeep(byte[] performData) {
        final Function function = new Function(
                FUNC_PERFORMUPKEEP, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicBytes(performData)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> processEarlyRepayment(BigInteger tokenId,
            BigInteger paymentAmount, BigInteger feeAmount) {
        final Function function = new Function(
                FUNC_PROCESSEARLYREPAYMENT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(tokenId), 
                new org.web3j.abi.datatypes.generated.Uint256(paymentAmount), 
                new org.web3j.abi.datatypes.generated.Uint256(feeAmount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> processRepayment(BigInteger tokenId) {
        final Function function = new Function(
                FUNC_PROCESSREPAYMENT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(tokenId)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<String> promissoryNoteAddress() {
        final Function function = new Function(FUNC_PROMISSORYNOTEADDRESS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<String> rabbitCoinAddress() {
        final Function function = new Function(FUNC_RABBITCOINADDRESS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> registerRepaymentSchedule(BigInteger tokenId) {
        final Function function = new Function(
                FUNC_REGISTERREPAYMENTSCHEDULE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(tokenId)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> renounceOwnership() {
        final Function function = new Function(
                FUNC_RENOUNCEOWNERSHIP, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    /* Tuple21이 Web3j에서 지원되지 않으므로 주석처리 후 새로 Function 생성 */

//    public RemoteFunctionCall<Tuple21<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, String, Boolean, Boolean, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger>> repaymentSchedules(
//            BigInteger param0) {
//        final Function function = new Function(FUNC_REPAYMENTSCHEDULES,
//                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(param0)),
//                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint8>() {}, new TypeReference<Address>() {}, new TypeReference<Bool>() {}, new TypeReference<Bool>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
//        return new RemoteFunctionCall<Tuple21<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, String, Boolean, Boolean, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger>>(function,
//                new Callable<Tuple21<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, String, Boolean, Boolean, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger>>() {
//                    @Override
//                    public Tuple21<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, String, Boolean, Boolean, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger> call(
//                            ) throws Exception {
//                        List<Type> results = executeCallMultipleValueReturn(function);
//                        return new Tuple21<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, String, Boolean, Boolean, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger>(
//                                (BigInteger) results.get(0).getValue(),
//                                (BigInteger) results.get(1).getValue(),
//                                (BigInteger) results.get(2).getValue(),
//                                (BigInteger) results.get(3).getValue(),
//                                (BigInteger) results.get(4).getValue(),
//                                (BigInteger) results.get(5).getValue(),
//                                (BigInteger) results.get(6).getValue(),
//                                (BigInteger) results.get(7).getValue(),
//                                (BigInteger) results.get(8).getValue(),
//                                (BigInteger) results.get(9).getValue(),
//                                (BigInteger) results.get(10).getValue(),
//                                (String) results.get(11).getValue(),
//                                (Boolean) results.get(12).getValue(),
//                                (Boolean) results.get(13).getValue(),
//                                (BigInteger) results.get(14).getValue(),
//                                (BigInteger) results.get(15).getValue(),
//                                (BigInteger) results.get(16).getValue(),
//                                (BigInteger) results.get(17).getValue(),
//                                (BigInteger) results.get(18).getValue(),
//                                (BigInteger) results.get(19).getValue(),
//                                (BigInteger) results.get(20).getValue());
//                    }
//                });
//    }


    public RemoteFunctionCall<RepaymentInfo> repaymentSchedules(
            BigInteger tokenId) {
        final Function function = new Function(
                FUNC_REPAYMENTSCHEDULES,
                Arrays.<Type>asList(new Uint256(tokenId)),
                Arrays.<TypeReference<?>>asList(
                        new TypeReference<Uint256>() {
                        }, new TypeReference<Uint256>() {
                        },
                        new TypeReference<Uint256>() {
                        }, new TypeReference<Uint256>() {
                        },
                        new TypeReference<Uint256>() {
                        }, new TypeReference<Uint256>() {
                        },
                        new TypeReference<Uint256>() {
                        }, new TypeReference<Uint256>() {
                        },
                        new TypeReference<Uint256>() {
                        }, new TypeReference<Uint256>() {
                        },
                        new TypeReference<Uint8>() {
                        }, new TypeReference<Address>() {
                        },
                        new TypeReference<Bool>() {
                        }, new TypeReference<Bool>() {
                        },
                        new TypeReference<Uint256>() {
                        }, new TypeReference<Uint256>() {
                        },
                        new TypeReference<Uint256>() {
                        }, new TypeReference<Uint256>() {
                        },
                        new TypeReference<Uint256>() {
                        }, new TypeReference<Uint256>() {
                        },
                        new TypeReference<Uint256>() {
                        }
                )
        );

        return new RemoteFunctionCall<>(function, () -> {
            List<Type> results = executeCallMultipleValueReturn(function);

            // RepaymentInfo 생성자에 Raw Type들을 전달 (Uint256, Address 등)
            return new RepaymentInfo(
                    (Uint256) results.get(0), (Uint256) results.get(1), (Uint256) results.get(2),
                    (Uint256) results.get(3), (Uint256) results.get(4), (Uint256) results.get(5),
                    (Uint256) results.get(6), (Uint256) results.get(7), (Uint256) results.get(8),
                    (Uint256) results.get(9), (Uint8) results.get(10), (Address) results.get(11),
                    (Bool) results.get(12), (Bool) results.get(13), (Uint256) results.get(14),
                    (Uint256) results.get(15), (Uint256) results.get(16), (Uint256) results.get(17),
                    (Uint256) results.get(18), (Uint256) results.get(19), (Uint256) results.get(20)
            );
        });
    }

    public RemoteFunctionCall<TransactionReceipt> transferOwnership(String newOwner) {
        final Function function = new Function(
                FUNC_TRANSFEROWNERSHIP, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, newOwner)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> updateContractAddresses(
            String _promissoryNoteAddress, String _rabbitCoinAddress) {
        final Function function = new Function(
                FUNC_UPDATECONTRACTADDRESSES, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _promissoryNoteAddress), 
                new org.web3j.abi.datatypes.Address(160, _rabbitCoinAddress)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> updateOverdueInfo(BigInteger tokenId,
            Boolean overdueFlag, BigInteger overdueStartDate, BigInteger overdueDays,
            BigInteger aoi, BigInteger defCnt, BigInteger currentIr, BigInteger totalDefCnt) {
        final Function function = new Function(
                FUNC_UPDATEOVERDUEINFO, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(tokenId), 
                new org.web3j.abi.datatypes.Bool(overdueFlag), 
                new org.web3j.abi.datatypes.generated.Uint256(overdueStartDate), 
                new org.web3j.abi.datatypes.generated.Uint256(overdueDays), 
                new org.web3j.abi.datatypes.generated.Uint256(aoi), 
                new org.web3j.abi.datatypes.generated.Uint256(defCnt), 
                new org.web3j.abi.datatypes.generated.Uint256(currentIr), 
                new org.web3j.abi.datatypes.generated.Uint256(totalDefCnt)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> updateRepaymentInfo(BigInteger tokenId,
            BigInteger remainingPrincipal, BigInteger remainingPayments,
            BigInteger nextPaymentDate) {
        final Function function = new Function(
                FUNC_UPDATEREPAYMENTINFO, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(tokenId), 
                new org.web3j.abi.datatypes.generated.Uint256(remainingPrincipal), 
                new org.web3j.abi.datatypes.generated.Uint256(remainingPayments), 
                new org.web3j.abi.datatypes.generated.Uint256(nextPaymentDate)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static RepaymentScheduler load(String contractAddress, Web3j web3j,
            Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new RepaymentScheduler(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static RepaymentScheduler load(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new RepaymentScheduler(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static RepaymentScheduler load(String contractAddress, Web3j web3j,
            Credentials credentials, ContractGasProvider contractGasProvider) {
        return new RepaymentScheduler(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static RepaymentScheduler load(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new RepaymentScheduler(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static class RepaymentInfo extends StaticStruct {
        public BigInteger tokenId;

        public BigInteger initialPrincipal;

        public BigInteger remainingPrincipal;

        public BigInteger ir;

        public BigInteger dir;

        public BigInteger mpDt;

        public BigInteger nextMpDt;

        public BigInteger totalPayments;

        public BigInteger remainingPayments;

        public BigInteger fixedPaymentAmount;

        public BigInteger repayType;

        public String drWalletAddress;

        public Boolean activeFlag;

        public Boolean overdueFlag;

        public BigInteger overdueStartDate;

        public BigInteger overdueDays;

        public BigInteger aoi;

        public BigInteger defCnt;

        public BigInteger accel;

        public BigInteger currentIr;

        public BigInteger totalDefCnt;

        public RepaymentInfo(BigInteger tokenId, BigInteger initialPrincipal,
                BigInteger remainingPrincipal, BigInteger ir, BigInteger dir, BigInteger mpDt,
                BigInteger nextMpDt, BigInteger totalPayments, BigInteger remainingPayments,
                BigInteger fixedPaymentAmount, BigInteger repayType, String drWalletAddress,
                Boolean activeFlag, Boolean overdueFlag, BigInteger overdueStartDate,
                BigInteger overdueDays, BigInteger aoi, BigInteger defCnt, BigInteger accel,
                BigInteger currentIr, BigInteger totalDefCnt) {
            super(new org.web3j.abi.datatypes.generated.Uint256(tokenId), 
                    new org.web3j.abi.datatypes.generated.Uint256(initialPrincipal), 
                    new org.web3j.abi.datatypes.generated.Uint256(remainingPrincipal), 
                    new org.web3j.abi.datatypes.generated.Uint256(ir), 
                    new org.web3j.abi.datatypes.generated.Uint256(dir), 
                    new org.web3j.abi.datatypes.generated.Uint256(mpDt), 
                    new org.web3j.abi.datatypes.generated.Uint256(nextMpDt), 
                    new org.web3j.abi.datatypes.generated.Uint256(totalPayments), 
                    new org.web3j.abi.datatypes.generated.Uint256(remainingPayments), 
                    new org.web3j.abi.datatypes.generated.Uint256(fixedPaymentAmount), 
                    new org.web3j.abi.datatypes.generated.Uint8(repayType), 
                    new org.web3j.abi.datatypes.Address(160, drWalletAddress), 
                    new org.web3j.abi.datatypes.Bool(activeFlag), 
                    new org.web3j.abi.datatypes.Bool(overdueFlag), 
                    new org.web3j.abi.datatypes.generated.Uint256(overdueStartDate), 
                    new org.web3j.abi.datatypes.generated.Uint256(overdueDays), 
                    new org.web3j.abi.datatypes.generated.Uint256(aoi), 
                    new org.web3j.abi.datatypes.generated.Uint256(defCnt), 
                    new org.web3j.abi.datatypes.generated.Uint256(accel), 
                    new org.web3j.abi.datatypes.generated.Uint256(currentIr), 
                    new org.web3j.abi.datatypes.generated.Uint256(totalDefCnt));
            this.tokenId = tokenId;
            this.initialPrincipal = initialPrincipal;
            this.remainingPrincipal = remainingPrincipal;
            this.ir = ir;
            this.dir = dir;
            this.mpDt = mpDt;
            this.nextMpDt = nextMpDt;
            this.totalPayments = totalPayments;
            this.remainingPayments = remainingPayments;
            this.fixedPaymentAmount = fixedPaymentAmount;
            this.repayType = repayType;
            this.drWalletAddress = drWalletAddress;
            this.activeFlag = activeFlag;
            this.overdueFlag = overdueFlag;
            this.overdueStartDate = overdueStartDate;
            this.overdueDays = overdueDays;
            this.aoi = aoi;
            this.defCnt = defCnt;
            this.accel = accel;
            this.currentIr = currentIr;
            this.totalDefCnt = totalDefCnt;
        }

        public RepaymentInfo(Uint256 tokenId, Uint256 initialPrincipal, Uint256 remainingPrincipal,
                Uint256 ir, Uint256 dir, Uint256 mpDt, Uint256 nextMpDt, Uint256 totalPayments,
                Uint256 remainingPayments, Uint256 fixedPaymentAmount, Uint8 repayType,
                Address drWalletAddress, Bool activeFlag, Bool overdueFlag,
                Uint256 overdueStartDate, Uint256 overdueDays, Uint256 aoi, Uint256 defCnt,
                Uint256 accel, Uint256 currentIr, Uint256 totalDefCnt) {
            super(tokenId, initialPrincipal, remainingPrincipal, ir, dir, mpDt, nextMpDt, totalPayments, remainingPayments, fixedPaymentAmount, repayType, drWalletAddress, activeFlag, overdueFlag, overdueStartDate, overdueDays, aoi, defCnt, accel, currentIr, totalDefCnt);
            this.tokenId = tokenId.getValue();
            this.initialPrincipal = initialPrincipal.getValue();
            this.remainingPrincipal = remainingPrincipal.getValue();
            this.ir = ir.getValue();
            this.dir = dir.getValue();
            this.mpDt = mpDt.getValue();
            this.nextMpDt = nextMpDt.getValue();
            this.totalPayments = totalPayments.getValue();
            this.remainingPayments = remainingPayments.getValue();
            this.fixedPaymentAmount = fixedPaymentAmount.getValue();
            this.repayType = repayType.getValue();
            this.drWalletAddress = drWalletAddress.getValue();
            this.activeFlag = activeFlag.getValue();
            this.overdueFlag = overdueFlag.getValue();
            this.overdueStartDate = overdueStartDate.getValue();
            this.overdueDays = overdueDays.getValue();
            this.aoi = aoi.getValue();
            this.defCnt = defCnt.getValue();
            this.accel = accel.getValue();
            this.currentIr = currentIr.getValue();
            this.totalDefCnt = totalDefCnt.getValue();
        }
    }

    public static class AccelReachedEventResponse extends BaseEventResponse {
        public BigInteger tokenId;

        public BigInteger maxInterestRate;
    }

    public static class BurnFailedEventResponse extends BaseEventResponse {
        public BigInteger tokenId;

        public String reason;
    }

    public static class EarlyRepaymentFeeEventResponse extends BaseEventResponse {
        public BigInteger tokenId;

        public BigInteger feeAmount;
    }

    public static class EarlyRepaymentPrincipalEventResponse extends BaseEventResponse {
        public BigInteger tokenId;

        public BigInteger principalAmount;

        public BigInteger remainingPrincipal;

        public Boolean isFullRepayment;
    }

    public static class InsufficientBalanceEventResponse extends BaseEventResponse {
        public BigInteger tokenId;

        public String debtor;

        public BigInteger requiredAmount;

        public BigInteger currentBalance;
    }

    public static class OverdueInfoUpdatedEventResponse extends BaseEventResponse {
        public BigInteger tokenId;

        public Boolean isOverdue;

        public BigInteger accumulatedOverdueInterest;
    }

    public static class OverdueInterestAccumulatedEventResponse extends BaseEventResponse {
        public BigInteger tokenId;

        public BigInteger newInterest;

        public BigInteger totalAccumulatedInterest;
    }

    public static class OverdueResolvedEventResponse extends BaseEventResponse {
        public BigInteger tokenId;

        public BigInteger paidOverdueAmount;
    }

    public static class OwnershipTransferredEventResponse extends BaseEventResponse {
        public String previousOwner;

        public String newOwner;
    }

    public static class RepaymentCompletedEventResponse extends BaseEventResponse {
        public BigInteger tokenId;
    }

    public static class RepaymentOverdueEventResponse extends BaseEventResponse {
        public BigInteger tokenId;

        public BigInteger overdueStartDate;

        public BigInteger totalDefaultCount;
    }

    public static class RepaymentProcessedEventResponse extends BaseEventResponse {
        public BigInteger tokenId;

        public BigInteger amount;

        public BigInteger remainingPrincipal;

        public BigInteger nextMpDt;
    }

    public static class RepaymentScheduleCreatedEventResponse extends BaseEventResponse {
        public BigInteger tokenId;

        public BigInteger remainingPrincipal;

        public BigInteger nextMpDt;
    }
}
