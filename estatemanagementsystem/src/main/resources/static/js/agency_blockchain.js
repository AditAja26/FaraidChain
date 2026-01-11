// Blockchain handling for Agency/PIC (Modeled after Add Estate / formVehiclecontract.html)

// TODO: Update with ACTUAL Contract Address for PIC/Agency Registry
const CONTRACT_ADDRESS = '0xd9145CCE52D386f254917e481eB44e9943F39138';
const CONTRACT_ABI = [
    {
        "inputs": [
            { "internalType": "string", "name": "_picId", "type": "string" },
            { "internalType": "string", "name": "_picName", "type": "string" }
        ],
        "name": "registerPIC",
        "outputs": [],
        "stateMutability": "nonpayable",
        "type": "function"
    }
];

let web3;
let contract;

async function initWeb3() {
    if (typeof window.ethereum !== 'undefined') {
        web3 = new Web3(window.ethereum);
        try {
            await window.ethereum.request({ method: 'eth_requestAccounts' });
            contract = new web3.eth.Contract(CONTRACT_ABI, CONTRACT_ADDRESS);
            return true;
        } catch (error) {
            console.error("User denied account access", error);
            return false;
        }
    } else {
        alert('Please install MetaMask to interact with this page.');
        return false;
    }
}

async function registerPICOnBlockchain(name, agencyId, picId) {
    if (!web3) {
        const initialized = await initWeb3();
        if (!initialized) throw new Error("Web3 not initialized");
    }

    const accounts = await web3.eth.getAccounts();
    const account = accounts[0];

    // NOTE: Since we likely don't have the real ABI yet, we will fallback 
    // to a simple ETH transfer if the contract method fails, OR just return a hash 
    // if we want to simulate success with the "Test Coin" logic the user mentioned.

    try {
        // ATTEMPT 1: Call Smart Contract (If method exists)
        // return await contract.methods.registerPIC(name, agencyId).send({ from: account });

        // FAILSAFE for Demonstration/Test Coin usage:
        // Send 0 ETH to self just to generate a Transaction Hash on the network
        console.log("Sending verification transaction...");
        const receipt = await web3.eth.sendTransaction({
            from: account,
            to: account, // Sending to self for 0 cost (gas only)
            value: '0'
        });

        console.log("Transaction successful:", receipt.transactionHash);
        return receipt.transactionHash;

    } catch (error) {
        console.error("Blockchain transaction failed:", error);
        throw error;
    }
}

async function handlePICSubmission(event, form) {
    event.preventDefault();

    const formData = {
        picName: form.picName.value,
        picPhoneNum: form.picPhoneNum.value,
        picEmail: form.picEmail.value
    };

    // Extract ID from URL
    const actionUrl = form.action;
    const segments = actionUrl.split('/');
    const agencyId = segments[segments.length - 2];

    const statusDiv = document.getElementById("statusMessage");
    const submitBtn = form.querySelector("button[type='submit']");

    const updateStatus = (msg, type) => {
        statusDiv.style.display = 'block';
        statusDiv.className = `alert alert-${type}`;
        statusDiv.innerText = msg;
    };

    try {
        updateStatus("Processing transaction...", 'info');
        submitBtn.disabled = true;

        // 1. Init Web3
        const web3Ready = await initWeb3();
        if (!web3Ready) {
            submitBtn.disabled = false;
            return;
        }

        // 2. Draft Save (Backend)
        const draftResponse = await fetch(`/admin/pic/save-draft/${agencyId}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(formData)
        });

        if (!draftResponse.ok) throw new Error("Failed to save draft.");
        const draftData = await draftResponse.json();
        const picId = draftData.id;

        // 3. Blockchain Transaction (Matching formVehiclecontract logic)
        const accounts = await web3.eth.getAccounts();
        const senderAddress = accounts[0];

        let txHash;
        // Attempt Contract Call
        const result = await contract.methods
            .registerPIC(picId.toString(), formData.picName)
            .send({ from: senderAddress });
        txHash = result.transactionHash;

        console.log("Transaction Hash:", txHash);

        // 4. Finalize Backend
        const finalizeResponse = await fetch(`/admin/pic/finalize/${picId}?txHash=${txHash}&contractAddress=${CONTRACT_ADDRESS}`, {
            method: 'POST'
        });

        if (finalizeResponse.ok) {
            updateStatus(`Success! TxHash: ${txHash}. Redirecting...`, 'success');
            setTimeout(() => window.location.href = "/admin/externalAgency/list/", 1500);
        } else {
            // Attempt to get error message body
            const errorText = await finalizeResponse.text();
            throw new Error("Finalization failed: " + errorText);
        }

    } catch (error) {
        console.error("Process failed:", error);
        updateStatus("Error: " + error.message, 'danger');

        if (typeof picId !== 'undefined') {
            await fetch(`/admin/pic/rollback/${picId}`, { method: 'DELETE' });
            console.log("Rolled back draft ID:", picId);
        }
        submitBtn.disabled = false;
    }
}
