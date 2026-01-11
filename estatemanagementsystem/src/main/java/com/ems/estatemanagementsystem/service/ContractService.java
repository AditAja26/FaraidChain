package com.ems.estatemanagementsystem.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.ems.estatemanagementsystem.entity.Contract;
import com.ems.estatemanagementsystem.entity.ContractDetail;
import com.ems.estatemanagementsystem.repository.ContractRepository;
import java.time.LocalDateTime;

import com.ems.estatemanagementsystem.pattern.Observer;
import com.ems.estatemanagementsystem.entity.PIC;
import com.ems.estatemanagementsystem.entity.ExternalAgency;

@Service
public class ContractService implements Observer {

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Save the contract with its details (address, date,)
    public void saveContract(String transactionHash, String contractDate, String contractAddress) {
        // Method 2: Using JdbcTemplate to handle the missing 'user_id' mapping in
        // Entity
        // We assign to Admin User (ID=1) or a System User because Agency transactions
        // are not tied to a specific end-user's will.
        String sql = "INSERT INTO contract (contract_date, transaction_hash, user_id, contract_address) VALUES (?, ?, ?, ?)";
        try {
            // Check if contract_address column exists or just try inserting
            jdbcTemplate.update(sql, contractDate, transactionHash, 1L, contractAddress);
            System.out.println("ContractService: Saved using JDBC with User ID 1 and Address " + contractAddress);
        } catch (Exception e) {
            System.err.println("ContractService JDBC Error: " + e.getMessage());
            // Retry without contract_address if calling failed?
            // If column missing, this will fail. We assume user has the column or wants it.
        }
    }

    public List<Contract> getAllContracts() {
        return contractRepository.findAll();
    }

    // public List<Contract> getContractsByUserId(Long userId) {
    // TODO Auto-generated method stub
    // throw new UnsupportedOperationException("Unimplemented method
    // 'getContractsByUserId'");
    // }

    @Override
    public void update(Object data) {
        String txHash = null;
        String contractAddress = null;

        if (data instanceof PIC) {
            txHash = ((PIC) data).getTxHash();
            contractAddress = ((PIC) data).getContractAddress();
        } else if (data instanceof ExternalAgency) {
            txHash = ((ExternalAgency) data).getTxHash();
            // ExternalAgency might not have setContractAddress yet, handle similarly if
            // needed
        }

        if (txHash != null && !txHash.isEmpty()) {
            System.out.println("ContractService: Recording transaction " + txHash + " to local ledger.");
            saveContract(txHash, LocalDateTime.now().toString(), contractAddress);
        }
    }
}
