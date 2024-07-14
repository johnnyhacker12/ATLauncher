package com.atlauncher.viewmodel.impl;

import com.atlauncher.data.AbstractAccount;
import com.atlauncher.data.LoginResponse;
import com.atlauncher.data.MojangAccount;
import com.atlauncher.managers.AccountManager;
import com.atlauncher.managers.LogManager;
import com.atlauncher.network.Analytics;
import com.atlauncher.network.analytics.AnalyticsEvent;

//Insufficient modularization
public class AccountOperation extends AccountsViewModel{
    private AccountsViewModel accountsViewModel;

    public AccountOperation(AccountsViewModel accountsViewModel) {
        this.accountsViewModel = accountsViewModel;
    }

    public void addNewAccount(LoginResponse response) {
        MojangAccount account = new MojangAccount(accountsViewModel.getLoginUsername(),
            accountsViewModel.getLoginPassword(),
            response,
            accountsViewModel.getRememberLogin(),
            accountsViewModel.getClientToken());

        AccountManager.addAccount(account);
        accountsViewModel.pushNewAccounts();
    }

    public void editAccount(LoginResponse response) {
        AbstractAccount account = accountsViewModel.getSelectedAccount();

        if (account instanceof MojangAccount) {
            MojangAccount mojangAccount = (MojangAccount) account;

            mojangAccount.username = accountsViewModel.getLoginUsername();
            mojangAccount.minecraftUsername = response.getAuth().getSelectedProfile().getName();
            mojangAccount.uuid = response.getAuth().getSelectedProfile().getId().toString();
            if (accountsViewModel.getRememberLogin()) {
                mojangAccount.setPassword(accountsViewModel.getLoginPassword());
            } else {
                mojangAccount.encryptedPassword = null;
                mojangAccount.password = null;
            }
            mojangAccount.remember = accountsViewModel.getRememberLogin();
            mojangAccount.clientToken = accountsViewModel.getClientToken();
            mojangAccount.store = response.getAuth().saveForStorage();

            AccountManager.saveAccounts();
        }

        Analytics.trackEvent(AnalyticsEvent.simpleEvent("account_edit"));
        LogManager.info("Edited Account " + account);
        accountsViewModel.pushNewAccounts();
    }
}