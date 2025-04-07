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
import org.web3j.abi.datatypes.DynamicStruct;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Bytes1;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple7;
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
public class PromissoryNote extends Contract {
    public static final String BINARY = "Bin file was not provided";

    public static final String FUNC_PERMIT_TYPEHASH = "PERMIT_TYPEHASH";

    public static final String FUNC_ADDBURNAUTHORIZATION = "addBurnAuthorization";

    public static final String FUNC_APPROVE = "approve";

    public static final String FUNC_BALANCEOF = "balanceOf";

    public static final String FUNC_BURN = "burn";

    public static final String FUNC_BURNAUTHORIZEDADDRESSES = "burnAuthorizedAddresses";

    public static final String FUNC_EIP712DOMAIN = "eip712Domain";

    public static final String FUNC_GETAPPENDIXMETADATA = "getAppendixMetadata";

    public static final String FUNC_GETAPPENDIXTOKENIDS = "getAppendixTokenIds";

    public static final String FUNC_GETAPPROVED = "getApproved";

    public static final String FUNC_GETLATESTAPPENDIXTOKENID = "getLatestAppendixTokenId";

    public static final String FUNC_GETLATESTCREDITORADDRESS = "getLatestCreditorAddress";

    public static final String FUNC_GETNONCE = "getNonce";

    public static final String FUNC_GETPERMITMESSAGEHASH = "getPermitMessageHash";

    public static final String FUNC_GETPROMISSORYMETADATA = "getPromissoryMetadata";

    public static final String FUNC_ISAPPROVEDFORALL = "isApprovedForAll";

    public static final String FUNC_MINT = "mint";

    public static final String FUNC_MINTAPPENDIXNFT = "mintAppendixNFT";

    public static final String FUNC_NAME = "name";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_OWNEROF = "ownerOf";

    public static final String FUNC_PERMIT = "permit";

    public static final String FUNC_REMOVEBURNAUTHORIZATION = "removeBurnAuthorization";

    public static final String FUNC_RENOUNCEOWNERSHIP = "renounceOwnership";

    public static final String FUNC_safeTransferFrom = "safeTransferFrom";

    public static final String FUNC_SCHEDULERADDRESS = "schedulerAddress";

    public static final String FUNC_SETAPPROVALFORALL = "setApprovalForAll";

    public static final String FUNC_SETSCHEDULERADDRESS = "setSchedulerAddress";

    public static final String FUNC_SUPPORTSINTERFACE = "supportsInterface";

    public static final String FUNC_SYMBOL = "symbol";

    public static final String FUNC_TOKENIDCOUNTER = "tokenIdCounter";

    public static final String FUNC_TOKENURI = "tokenURI";

    public static final String FUNC_TRANSFERFROM = "transferFrom";

    public static final String FUNC_TRANSFEROWNERSHIP = "transferOwnership";

    public static final Event APPENDIXNFTMINTED_EVENT = new Event("AppendixNFTMinted", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>(true) {}, new TypeReference<Uint256>(true) {}, new TypeReference<Address>() {}, new TypeReference<Address>(true) {}));
    ;

    public static final Event APPROVAL_EVENT = new Event("Approval", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Uint256>(true) {}));
    ;

    public static final Event APPROVALFORALL_EVENT = new Event("ApprovalForAll", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Bool>() {}));
    ;

    public static final Event EIP712DOMAINCHANGED_EVENT = new Event("EIP712DomainChanged", 
            Arrays.<TypeReference<?>>asList());
    ;

    public static final Event OWNERSHIPTRANSFERRED_EVENT = new Event("OwnershipTransferred", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}));
    ;

    public static final Event PROMISSORYNOTEBURNED_EVENT = new Event("PromissoryNoteBurned", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>(true) {}));
    ;

    public static final Event PROMISSORYNOTEMINTED_EVENT = new Event("PromissoryNoteMinted", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<PromissoryMetadata>() {}));
    ;

    public static final Event TRANSFER_EVENT = new Event("Transfer", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Uint256>(true) {}));
    ;

    @Deprecated
    protected PromissoryNote(String contractAddress, Web3j web3j, Credentials credentials,
            BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected PromissoryNote(String contractAddress, Web3j web3j, Credentials credentials,
            ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected PromissoryNote(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected PromissoryNote(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static List<AppendixNFTMintedEventResponse> getAppendixNFTMintedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(APPENDIXNFTMINTED_EVENT, transactionReceipt);
        ArrayList<AppendixNFTMintedEventResponse> responses = new ArrayList<AppendixNFTMintedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            AppendixNFTMintedEventResponse typedResponse = new AppendixNFTMintedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.appendixTokenId = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.originalTokenId = (BigInteger) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.newOwner = (String) eventValues.getIndexedValues().get(2).getValue();
            typedResponse.from = (String) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static AppendixNFTMintedEventResponse getAppendixNFTMintedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(APPENDIXNFTMINTED_EVENT, log);
        AppendixNFTMintedEventResponse typedResponse = new AppendixNFTMintedEventResponse();
        typedResponse.log = log;
        typedResponse.appendixTokenId = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.originalTokenId = (BigInteger) eventValues.getIndexedValues().get(1).getValue();
        typedResponse.newOwner = (String) eventValues.getIndexedValues().get(2).getValue();
        typedResponse.from = (String) eventValues.getNonIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public Flowable<AppendixNFTMintedEventResponse> appendixNFTMintedEventFlowable(
            EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getAppendixNFTMintedEventFromLog(log));
    }

    public Flowable<AppendixNFTMintedEventResponse> appendixNFTMintedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(APPENDIXNFTMINTED_EVENT));
        return appendixNFTMintedEventFlowable(filter);
    }

    public static List<ApprovalEventResponse> getApprovalEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(APPROVAL_EVENT, transactionReceipt);
        ArrayList<ApprovalEventResponse> responses = new ArrayList<ApprovalEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            ApprovalEventResponse typedResponse = new ApprovalEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.owner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.approved = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.tokenId = (BigInteger) eventValues.getIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static ApprovalEventResponse getApprovalEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(APPROVAL_EVENT, log);
        ApprovalEventResponse typedResponse = new ApprovalEventResponse();
        typedResponse.log = log;
        typedResponse.owner = (String) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.approved = (String) eventValues.getIndexedValues().get(1).getValue();
        typedResponse.tokenId = (BigInteger) eventValues.getIndexedValues().get(2).getValue();
        return typedResponse;
    }

    public Flowable<ApprovalEventResponse> approvalEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getApprovalEventFromLog(log));
    }

    public Flowable<ApprovalEventResponse> approvalEventFlowable(DefaultBlockParameter startBlock,
            DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(APPROVAL_EVENT));
        return approvalEventFlowable(filter);
    }

    public static List<ApprovalForAllEventResponse> getApprovalForAllEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(APPROVALFORALL_EVENT, transactionReceipt);
        ArrayList<ApprovalForAllEventResponse> responses = new ArrayList<ApprovalForAllEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            ApprovalForAllEventResponse typedResponse = new ApprovalForAllEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.owner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.operator = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.approved = (Boolean) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static ApprovalForAllEventResponse getApprovalForAllEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(APPROVALFORALL_EVENT, log);
        ApprovalForAllEventResponse typedResponse = new ApprovalForAllEventResponse();
        typedResponse.log = log;
        typedResponse.owner = (String) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.operator = (String) eventValues.getIndexedValues().get(1).getValue();
        typedResponse.approved = (Boolean) eventValues.getNonIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public Flowable<ApprovalForAllEventResponse> approvalForAllEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getApprovalForAllEventFromLog(log));
    }

    public Flowable<ApprovalForAllEventResponse> approvalForAllEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(APPROVALFORALL_EVENT));
        return approvalForAllEventFlowable(filter);
    }

    public static List<EIP712DomainChangedEventResponse> getEIP712DomainChangedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(EIP712DOMAINCHANGED_EVENT, transactionReceipt);
        ArrayList<EIP712DomainChangedEventResponse> responses = new ArrayList<EIP712DomainChangedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            EIP712DomainChangedEventResponse typedResponse = new EIP712DomainChangedEventResponse();
            typedResponse.log = eventValues.getLog();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static EIP712DomainChangedEventResponse getEIP712DomainChangedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(EIP712DOMAINCHANGED_EVENT, log);
        EIP712DomainChangedEventResponse typedResponse = new EIP712DomainChangedEventResponse();
        typedResponse.log = log;
        return typedResponse;
    }

    public Flowable<EIP712DomainChangedEventResponse> eIP712DomainChangedEventFlowable(
            EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getEIP712DomainChangedEventFromLog(log));
    }

    public Flowable<EIP712DomainChangedEventResponse> eIP712DomainChangedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(EIP712DOMAINCHANGED_EVENT));
        return eIP712DomainChangedEventFlowable(filter);
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

    public static List<PromissoryNoteBurnedEventResponse> getPromissoryNoteBurnedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(PROMISSORYNOTEBURNED_EVENT, transactionReceipt);
        ArrayList<PromissoryNoteBurnedEventResponse> responses = new ArrayList<PromissoryNoteBurnedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            PromissoryNoteBurnedEventResponse typedResponse = new PromissoryNoteBurnedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.tokenId = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static PromissoryNoteBurnedEventResponse getPromissoryNoteBurnedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(PROMISSORYNOTEBURNED_EVENT, log);
        PromissoryNoteBurnedEventResponse typedResponse = new PromissoryNoteBurnedEventResponse();
        typedResponse.log = log;
        typedResponse.tokenId = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public Flowable<PromissoryNoteBurnedEventResponse> promissoryNoteBurnedEventFlowable(
            EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getPromissoryNoteBurnedEventFromLog(log));
    }

    public Flowable<PromissoryNoteBurnedEventResponse> promissoryNoteBurnedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(PROMISSORYNOTEBURNED_EVENT));
        return promissoryNoteBurnedEventFlowable(filter);
    }

    public static List<PromissoryNoteMintedEventResponse> getPromissoryNoteMintedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(PROMISSORYNOTEMINTED_EVENT, transactionReceipt);
        ArrayList<PromissoryNoteMintedEventResponse> responses = new ArrayList<PromissoryNoteMintedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            PromissoryNoteMintedEventResponse typedResponse = new PromissoryNoteMintedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.tokenId = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.to = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.metadata = (PromissoryMetadata) eventValues.getNonIndexedValues().get(0);
            responses.add(typedResponse);
        }
        return responses;
    }

    public static PromissoryNoteMintedEventResponse getPromissoryNoteMintedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(PROMISSORYNOTEMINTED_EVENT, log);
        PromissoryNoteMintedEventResponse typedResponse = new PromissoryNoteMintedEventResponse();
        typedResponse.log = log;
        typedResponse.tokenId = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.to = (String) eventValues.getIndexedValues().get(1).getValue();
        typedResponse.metadata = (PromissoryMetadata) eventValues.getNonIndexedValues().get(0);
        return typedResponse;
    }

    public Flowable<PromissoryNoteMintedEventResponse> promissoryNoteMintedEventFlowable(
            EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getPromissoryNoteMintedEventFromLog(log));
    }

    public Flowable<PromissoryNoteMintedEventResponse> promissoryNoteMintedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(PROMISSORYNOTEMINTED_EVENT));
        return promissoryNoteMintedEventFlowable(filter);
    }

    public static List<TransferEventResponse> getTransferEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(TRANSFER_EVENT, transactionReceipt);
        ArrayList<TransferEventResponse> responses = new ArrayList<TransferEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            TransferEventResponse typedResponse = new TransferEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.to = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.tokenId = (BigInteger) eventValues.getIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static TransferEventResponse getTransferEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(TRANSFER_EVENT, log);
        TransferEventResponse typedResponse = new TransferEventResponse();
        typedResponse.log = log;
        typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.to = (String) eventValues.getIndexedValues().get(1).getValue();
        typedResponse.tokenId = (BigInteger) eventValues.getIndexedValues().get(2).getValue();
        return typedResponse;
    }

    public Flowable<TransferEventResponse> transferEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getTransferEventFromLog(log));
    }

    public Flowable<TransferEventResponse> transferEventFlowable(DefaultBlockParameter startBlock,
            DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(TRANSFER_EVENT));
        return transferEventFlowable(filter);
    }

    public RemoteFunctionCall<byte[]> PERMIT_TYPEHASH() {
        final Function function = new Function(FUNC_PERMIT_TYPEHASH, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteFunctionCall<TransactionReceipt> addBurnAuthorization(String authorized) {
        final Function function = new Function(
                FUNC_ADDBURNAUTHORIZATION, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, authorized)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> approve(String to, BigInteger tokenId) {
        final Function function = new Function(
                FUNC_APPROVE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, to), 
                new org.web3j.abi.datatypes.generated.Uint256(tokenId)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> balanceOf(String owner) {
        final Function function = new Function(FUNC_BALANCEOF, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, owner)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> burn(BigInteger tokenId) {
        final Function function = new Function(
                FUNC_BURN, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(tokenId)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<Boolean> burnAuthorizedAddresses(String param0) {
        final Function function = new Function(FUNC_BURNAUTHORIZEDADDRESSES, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<Tuple7<byte[], String, String, BigInteger, String, byte[], List<BigInteger>>> eip712Domain(
            ) {
        final Function function = new Function(FUNC_EIP712DOMAIN, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes1>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Address>() {}, new TypeReference<Bytes32>() {}, new TypeReference<DynamicArray<Uint256>>() {}));
        return new RemoteFunctionCall<Tuple7<byte[], String, String, BigInteger, String, byte[], List<BigInteger>>>(function,
                new Callable<Tuple7<byte[], String, String, BigInteger, String, byte[], List<BigInteger>>>() {
                    @Override
                    public Tuple7<byte[], String, String, BigInteger, String, byte[], List<BigInteger>> call(
                            ) throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple7<byte[], String, String, BigInteger, String, byte[], List<BigInteger>>(
                                (byte[]) results.get(0).getValue(), 
                                (String) results.get(1).getValue(), 
                                (String) results.get(2).getValue(), 
                                (BigInteger) results.get(3).getValue(), 
                                (String) results.get(4).getValue(), 
                                (byte[]) results.get(5).getValue(), 
                                convertToNative((List<Uint256>) results.get(6).getValue()));
                    }
                });
    }

    public RemoteFunctionCall<AppendixMetadata> getAppendixMetadata(BigInteger appendixTokenId) {
        final Function function = new Function(FUNC_GETAPPENDIXMETADATA, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(appendixTokenId)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<AppendixMetadata>() {}));
        return executeRemoteCallSingleValueReturn(function, AppendixMetadata.class);
    }

    public RemoteFunctionCall<List> getAppendixTokenIds(BigInteger originalTokenId) {
        final Function function = new Function(FUNC_GETAPPENDIXTOKENIDS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(originalTokenId)), 
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

    public RemoteFunctionCall<String> getApproved(BigInteger tokenId) {
        final Function function = new Function(FUNC_GETAPPROVED, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(tokenId)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<BigInteger> getLatestAppendixTokenId(BigInteger originalTokenId) {
        final Function function = new Function(FUNC_GETLATESTAPPENDIXTOKENID, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(originalTokenId)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<String> getLatestCreditorAddress(BigInteger originalTokenId) {
        final Function function = new Function(FUNC_GETLATESTCREDITORADDRESS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(originalTokenId)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<BigInteger> getNonce(String user) {
        final Function function = new Function(FUNC_GETNONCE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, user)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<byte[]> getPermitMessageHash(String owner, String spender,
            BigInteger tokenId, BigInteger nonce, BigInteger deadline) {
        final Function function = new Function(FUNC_GETPERMITMESSAGEHASH, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, owner), 
                new org.web3j.abi.datatypes.Address(160, spender), 
                new org.web3j.abi.datatypes.generated.Uint256(tokenId), 
                new org.web3j.abi.datatypes.generated.Uint256(nonce), 
                new org.web3j.abi.datatypes.generated.Uint256(deadline)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteFunctionCall<PromissoryMetadata> getPromissoryMetadata(BigInteger tokenId) {
        final Function function = new Function(FUNC_GETPROMISSORYMETADATA, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(tokenId)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<PromissoryMetadata>() {}));
        return executeRemoteCallSingleValueReturn(function, PromissoryMetadata.class);
    }

    public RemoteFunctionCall<Boolean> isApprovedForAll(String owner, String operator) {
        final Function function = new Function(FUNC_ISAPPROVEDFORALL, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, owner), 
                new org.web3j.abi.datatypes.Address(160, operator)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<TransactionReceipt> mint(PromissoryMetadata metadata, String to) {
        final Function function = new Function(
                FUNC_MINT, 
                Arrays.<Type>asList(metadata, 
                new org.web3j.abi.datatypes.Address(160, to)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> mintAppendixNFT(BigInteger originalTokenId,
            AppendixMetadata metadata, String recipient) {
        final Function function = new Function(
                FUNC_MINTAPPENDIXNFT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(originalTokenId), 
                metadata, 
                new org.web3j.abi.datatypes.Address(160, recipient)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<String> name() {
        final Function function = new Function(FUNC_NAME, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<String> owner() {
        final Function function = new Function(FUNC_OWNER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<String> ownerOf(BigInteger tokenId) {
        final Function function = new Function(FUNC_OWNEROF, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(tokenId)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> permit(String owner, String spender,
            BigInteger tokenId, BigInteger deadline, byte[] signature) {
        final Function function = new Function(
                FUNC_PERMIT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, owner), 
                new org.web3j.abi.datatypes.Address(160, spender), 
                new org.web3j.abi.datatypes.generated.Uint256(tokenId), 
                new org.web3j.abi.datatypes.generated.Uint256(deadline), 
                new org.web3j.abi.datatypes.DynamicBytes(signature)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> removeBurnAuthorization(String authorized) {
        final Function function = new Function(
                FUNC_REMOVEBURNAUTHORIZATION, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, authorized)), 
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

    public RemoteFunctionCall<TransactionReceipt> safeTransferFrom(String from, String to,
            BigInteger tokenId) {
        final Function function = new Function(
                FUNC_safeTransferFrom, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, from), 
                new org.web3j.abi.datatypes.Address(160, to), 
                new org.web3j.abi.datatypes.generated.Uint256(tokenId)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> safeTransferFrom(String from, String to,
            BigInteger tokenId, byte[] data) {
        final Function function = new Function(
                FUNC_safeTransferFrom, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, from), 
                new org.web3j.abi.datatypes.Address(160, to), 
                new org.web3j.abi.datatypes.generated.Uint256(tokenId), 
                new org.web3j.abi.datatypes.DynamicBytes(data)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<String> schedulerAddress() {
        final Function function = new Function(FUNC_SCHEDULERADDRESS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> setApprovalForAll(String operator,
            Boolean approved) {
        final Function function = new Function(
                FUNC_SETAPPROVALFORALL, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, operator), 
                new org.web3j.abi.datatypes.Bool(approved)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> setSchedulerAddress(String _schedulerAddress) {
        final Function function = new Function(
                FUNC_SETSCHEDULERADDRESS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _schedulerAddress)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<Boolean> supportsInterface(byte[] interfaceId) {
        final Function function = new Function(FUNC_SUPPORTSINTERFACE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes4(interfaceId)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<String> symbol() {
        final Function function = new Function(FUNC_SYMBOL, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<BigInteger> tokenIdCounter() {
        final Function function = new Function(FUNC_TOKENIDCOUNTER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<String> tokenURI(BigInteger tokenId) {
        final Function function = new Function(FUNC_TOKENURI, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(tokenId)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> transferFrom(String from, String to,
            BigInteger tokenId) {
        final Function function = new Function(
                FUNC_TRANSFERFROM, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, from), 
                new org.web3j.abi.datatypes.Address(160, to), 
                new org.web3j.abi.datatypes.generated.Uint256(tokenId)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> transferOwnership(String newOwner) {
        final Function function = new Function(
                FUNC_TRANSFEROWNERSHIP, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, newOwner)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static PromissoryNote load(String contractAddress, Web3j web3j, Credentials credentials,
            BigInteger gasPrice, BigInteger gasLimit) {
        return new PromissoryNote(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static PromissoryNote load(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new PromissoryNote(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static PromissoryNote load(String contractAddress, Web3j web3j, Credentials credentials,
            ContractGasProvider contractGasProvider) {
        return new PromissoryNote(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static PromissoryNote load(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new PromissoryNote(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static class CrInfo extends DynamicStruct {
        public String crSign;

        public String crName;

        public String crWalletAddress;

        public String crInfoHash;

        public CrInfo(String crSign, String crName, String crWalletAddress, String crInfoHash) {
            super(new org.web3j.abi.datatypes.Utf8String(crSign), 
                    new org.web3j.abi.datatypes.Utf8String(crName), 
                    new org.web3j.abi.datatypes.Address(160, crWalletAddress), 
                    new org.web3j.abi.datatypes.Utf8String(crInfoHash));
            this.crSign = crSign;
            this.crName = crName;
            this.crWalletAddress = crWalletAddress;
            this.crInfoHash = crInfoHash;
        }

        public CrInfo(Utf8String crSign, Utf8String crName, Address crWalletAddress,
                Utf8String crInfoHash) {
            super(crSign, crName, crWalletAddress, crInfoHash);
            this.crSign = crSign.getValue();
            this.crName = crName.getValue();
            this.crWalletAddress = crWalletAddress.getValue();
            this.crInfoHash = crInfoHash.getValue();
        }
    }

    public static class DrInfo extends DynamicStruct {
        public String drSign;

        public String drName;

        public String drWalletAddress;

        public String drInfoHash;

        public DrInfo(String drSign, String drName, String drWalletAddress, String drInfoHash) {
            super(new org.web3j.abi.datatypes.Utf8String(drSign), 
                    new org.web3j.abi.datatypes.Utf8String(drName), 
                    new org.web3j.abi.datatypes.Address(160, drWalletAddress), 
                    new org.web3j.abi.datatypes.Utf8String(drInfoHash));
            this.drSign = drSign;
            this.drName = drName;
            this.drWalletAddress = drWalletAddress;
            this.drInfoHash = drInfoHash;
        }

        public DrInfo(Utf8String drSign, Utf8String drName, Address drWalletAddress,
                Utf8String drInfoHash) {
            super(drSign, drName, drWalletAddress, drInfoHash);
            this.drSign = drSign.getValue();
            this.drName = drName.getValue();
            this.drWalletAddress = drWalletAddress.getValue();
            this.drInfoHash = drInfoHash.getValue();
        }
    }

    public static class AddTerms extends DynamicStruct {
        public String addTerms;

        public String addTermsHash;

        public AddTerms(String addTerms, String addTermsHash) {
            super(new org.web3j.abi.datatypes.Utf8String(addTerms), 
                    new org.web3j.abi.datatypes.Utf8String(addTermsHash));
            this.addTerms = addTerms;
            this.addTermsHash = addTermsHash;
        }

        public AddTerms(Utf8String addTerms, Utf8String addTermsHash) {
            super(addTerms, addTermsHash);
            this.addTerms = addTerms.getValue();
            this.addTermsHash = addTermsHash.getValue();
        }
    }

    public static class AppendixMetadata extends DynamicStruct {
        public BigInteger tokenId;

        public String grantorSign;

        public String grantorName;

        public String grantorWalletAddress;

        public String grantorInfoHash;

        public String granteeSign;

        public String granteeName;

        public String granteeWalletAddress;

        public String granteeInfoHash;

        public BigInteger la;

        public String contractDate;

        public String originalText;

        public AppendixMetadata(BigInteger tokenId, String grantorSign, String grantorName,
                String grantorWalletAddress, String grantorInfoHash, String granteeSign,
                String granteeName, String granteeWalletAddress, String granteeInfoHash,
                BigInteger la, String contractDate, String originalText) {
            super(new org.web3j.abi.datatypes.generated.Uint256(tokenId), 
                    new org.web3j.abi.datatypes.Utf8String(grantorSign), 
                    new org.web3j.abi.datatypes.Utf8String(grantorName), 
                    new org.web3j.abi.datatypes.Address(160, grantorWalletAddress), 
                    new org.web3j.abi.datatypes.Utf8String(grantorInfoHash), 
                    new org.web3j.abi.datatypes.Utf8String(granteeSign), 
                    new org.web3j.abi.datatypes.Utf8String(granteeName), 
                    new org.web3j.abi.datatypes.Address(160, granteeWalletAddress), 
                    new org.web3j.abi.datatypes.Utf8String(granteeInfoHash), 
                    new org.web3j.abi.datatypes.generated.Uint256(la), 
                    new org.web3j.abi.datatypes.Utf8String(contractDate), 
                    new org.web3j.abi.datatypes.Utf8String(originalText));
            this.tokenId = tokenId;
            this.grantorSign = grantorSign;
            this.grantorName = grantorName;
            this.grantorWalletAddress = grantorWalletAddress;
            this.grantorInfoHash = grantorInfoHash;
            this.granteeSign = granteeSign;
            this.granteeName = granteeName;
            this.granteeWalletAddress = granteeWalletAddress;
            this.granteeInfoHash = granteeInfoHash;
            this.la = la;
            this.contractDate = contractDate;
            this.originalText = originalText;
        }

        public AppendixMetadata(Uint256 tokenId, Utf8String grantorSign, Utf8String grantorName,
                Address grantorWalletAddress, Utf8String grantorInfoHash, Utf8String granteeSign,
                Utf8String granteeName, Address granteeWalletAddress, Utf8String granteeInfoHash,
                Uint256 la, Utf8String contractDate, Utf8String originalText) {
            super(tokenId, grantorSign, grantorName, grantorWalletAddress, grantorInfoHash, granteeSign, granteeName, granteeWalletAddress, granteeInfoHash, la, contractDate, originalText);
            this.tokenId = tokenId.getValue();
            this.grantorSign = grantorSign.getValue();
            this.grantorName = grantorName.getValue();
            this.grantorWalletAddress = grantorWalletAddress.getValue();
            this.grantorInfoHash = grantorInfoHash.getValue();
            this.granteeSign = granteeSign.getValue();
            this.granteeName = granteeName.getValue();
            this.granteeWalletAddress = granteeWalletAddress.getValue();
            this.granteeInfoHash = granteeInfoHash.getValue();
            this.la = la.getValue();
            this.contractDate = contractDate.getValue();
            this.originalText = originalText.getValue();
        }
    }

    public static class PromissoryMetadata extends DynamicStruct {
        public String nftImage;

        public CrInfo crInfo;

        public DrInfo drInfo;

        public BigInteger la;

        public BigInteger ir;

        public BigInteger lt;

        public String repayType;

        public String matDt;

        public BigInteger mpDt;

        public BigInteger dir;

        public String contractDate;

        public Boolean earlyPayFlag;

        public BigInteger earlyPayFee;

        public BigInteger accel;

        public AddTerms addTerms;

        public PromissoryMetadata(String nftImage, CrInfo crInfo, DrInfo drInfo, BigInteger la,
                BigInteger ir, BigInteger lt, String repayType, String matDt, BigInteger mpDt,
                BigInteger dir, String contractDate, Boolean earlyPayFlag, BigInteger earlyPayFee,
                BigInteger accel, AddTerms addTerms) {
            super(new org.web3j.abi.datatypes.Utf8String(nftImage), 
                    crInfo, 
                    drInfo, 
                    new org.web3j.abi.datatypes.generated.Uint256(la), 
                    new org.web3j.abi.datatypes.generated.Uint256(ir), 
                    new org.web3j.abi.datatypes.generated.Uint256(lt), 
                    new org.web3j.abi.datatypes.Utf8String(repayType), 
                    new org.web3j.abi.datatypes.Utf8String(matDt), 
                    new org.web3j.abi.datatypes.generated.Uint256(mpDt), 
                    new org.web3j.abi.datatypes.generated.Uint256(dir), 
                    new org.web3j.abi.datatypes.Utf8String(contractDate), 
                    new org.web3j.abi.datatypes.Bool(earlyPayFlag), 
                    new org.web3j.abi.datatypes.generated.Uint256(earlyPayFee), 
                    new org.web3j.abi.datatypes.generated.Uint256(accel), 
                    addTerms);
            this.nftImage = nftImage;
            this.crInfo = crInfo;
            this.drInfo = drInfo;
            this.la = la;
            this.ir = ir;
            this.lt = lt;
            this.repayType = repayType;
            this.matDt = matDt;
            this.mpDt = mpDt;
            this.dir = dir;
            this.contractDate = contractDate;
            this.earlyPayFlag = earlyPayFlag;
            this.earlyPayFee = earlyPayFee;
            this.accel = accel;
            this.addTerms = addTerms;
        }

        public PromissoryMetadata(Utf8String nftImage, CrInfo crInfo, DrInfo drInfo, Uint256 la,
                Uint256 ir, Uint256 lt, Utf8String repayType, Utf8String matDt, Uint256 mpDt,
                Uint256 dir, Utf8String contractDate, Bool earlyPayFlag, Uint256 earlyPayFee,
                Uint256 accel, AddTerms addTerms) {
            super(nftImage, crInfo, drInfo, la, ir, lt, repayType, matDt, mpDt, dir, contractDate, earlyPayFlag, earlyPayFee, accel, addTerms);
            this.nftImage = nftImage.getValue();
            this.crInfo = crInfo;
            this.drInfo = drInfo;
            this.la = la.getValue();
            this.ir = ir.getValue();
            this.lt = lt.getValue();
            this.repayType = repayType.getValue();
            this.matDt = matDt.getValue();
            this.mpDt = mpDt.getValue();
            this.dir = dir.getValue();
            this.contractDate = contractDate.getValue();
            this.earlyPayFlag = earlyPayFlag.getValue();
            this.earlyPayFee = earlyPayFee.getValue();
            this.accel = accel.getValue();
            this.addTerms = addTerms;
        }
    }

    public static class AppendixNFTMintedEventResponse extends BaseEventResponse {
        public BigInteger appendixTokenId;

        public BigInteger originalTokenId;

        public String newOwner;

        public String from;
    }

    public static class ApprovalEventResponse extends BaseEventResponse {
        public String owner;

        public String approved;

        public BigInteger tokenId;
    }

    public static class ApprovalForAllEventResponse extends BaseEventResponse {
        public String owner;

        public String operator;

        public Boolean approved;
    }

    public static class EIP712DomainChangedEventResponse extends BaseEventResponse {
    }

    public static class OwnershipTransferredEventResponse extends BaseEventResponse {
        public String previousOwner;

        public String newOwner;
    }

    public static class PromissoryNoteBurnedEventResponse extends BaseEventResponse {
        public BigInteger tokenId;
    }

    public static class PromissoryNoteMintedEventResponse extends BaseEventResponse {
        public BigInteger tokenId;

        public String to;

        public PromissoryMetadata metadata;
    }

    public static class TransferEventResponse extends BaseEventResponse {
        public String from;

        public String to;

        public BigInteger tokenId;
    }
}
