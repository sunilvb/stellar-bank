package com.sb.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.stellar.sdk.AssetTypeNative;
import org.stellar.sdk.KeyPair;
import org.stellar.sdk.Memo;
import org.stellar.sdk.Network;
import org.stellar.sdk.PaymentOperation;
import org.stellar.sdk.Server;
import org.stellar.sdk.Transaction;
import org.stellar.sdk.responses.AccountResponse;
import org.stellar.sdk.responses.SubmitTransactionResponse;

import com.sb.model.Account;

import com.sb.repository.AccountRepository;

@Service("accountService")
public class AccountService {

	@Autowired
	private AccountRepository accountRepository;

	@Value("${stellar.network.url}")
	private String network;

	@Value("${stellar.network.friendbot}")
	private String friendbot;

	public List<Account> findByEmail(String email) {
		return accountRepository.findByEmail(email);
	}

	public float getTotalBalance(String email) {
		float total = 0;
		List<Account> lst = accountRepository.findByEmail(email);
		for (Account acc : lst)
			total += getBalance(acc.getPublicKey());

		return total;

	}

	public String getNetwork() {
		return network;
	}

	public void setNetwork(String network) {
		this.network = network;
	}

	public String getFriendbot() {
		return friendbot;
	}

	public void setFriendbot(String friendbot) {
		this.friendbot = friendbot;
	}

	public String openAccount(String name, String email) {

		String key = null;
		InputStream response = null;
		try {

			KeyPair pair = KeyPair.random();
			String seed = new String(pair.getSecretSeed());
			key = pair.getAccountId();
			String friendbotUrl = String.format(friendbot, key);

			response = new URL(friendbotUrl).openStream();
			String body = new Scanner(response, "UTF-8").useDelimiter("\\A").next();
			System.out.println("New Stellar account created :)\n" + body);

			Account acc = new Account(key, seed, name, email);
			accountRepository.save(acc);
			response.close();
		} catch (MalformedURLException e) {
			key = null;
			e.printStackTrace();
		} catch (IOException e) {
			key = null;
			e.printStackTrace();
		}

		return key;
	}

	public float getBalance(String accountKey) {
		float ff = 0;
		Network.useTestNetwork();
		Server server = new Server(network);
		AccountResponse account = null;
		KeyPair destination = KeyPair.fromAccountId(accountKey);

		System.out.println("Using network : " + network);
		try {
			account = server.accounts().account(destination);
			System.out.println("Balances for account " + accountKey);
			for (AccountResponse.Balance balance : account.getBalances()) {
				ff += Float.parseFloat(balance.getBalance());
				System.out.println(String.format("Type: %s, Code: %s, Balance: %s", balance.getAssetType(),
						balance.getAssetCode(), ff));
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ff;
	}

	public boolean transferFunds(String from, String to, String amount, String memo) {
		System.out.println(" Transfering Lumens from : " +from);
		System.out.println(" Transfering Lumens to : " +to);
		System.out.println(" Transfering Lumens amount : " +amount);
		System.out.println(" Transfering Lumens memo : " +memo);
		Network.useTestNetwork();
		Server server = new Server(network);
		KeyPair source = KeyPair.fromSecretSeed(from);
		KeyPair destination = KeyPair.fromAccountId(to);
		AccountResponse sourceAccount = null;
		try {
			sourceAccount = server.accounts().account(source);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// Start building the transaction.
		Transaction transaction = new Transaction.Builder(sourceAccount)
				.addOperation(new PaymentOperation.Builder(destination, new AssetTypeNative(), amount).build())
				// A memo allows you to add your own metadata to a transaction.
				// It's
				// optional and does not affect how Stellar treats the
				// transaction.
				.addMemo(Memo.text(memo)).build();
		// Sign the transaction to prove you are actually the person sending it.
		transaction.sign(source);

		// And finally, send it off to Stellar!
		try {
			SubmitTransactionResponse response = server.submitTransaction(transaction);
			System.out.println("Was submitTransaction successful : ");
			System.out.print(response.isSuccess() + " " + response.getResultXdr());
		} catch (Exception e) {
			System.out.println("Something went wrong!");
			System.out.println(e.getMessage());
			// If the result is unknown (no response body, timeout etc.) we
			// simply resubmit
			// already built transaction:
			// SubmitTransactionResponse response =
			// server.submitTransaction(transaction);
		}

		return true;
	}
	

}
