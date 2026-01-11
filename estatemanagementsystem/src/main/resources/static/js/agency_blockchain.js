// agency_blockchain.js

// Contract Address
const AGENCY_CONTRACT_ADDRESS = "0xd9145CCE52D386f254917e481eB44e9943F39138";

// ABI from provided AgencyRegistry.sol
const AGENCY_CONTRACT_ABI = [
    {
        "anonymous": false,
        "inputs": [
            {
                "indexed": true,
                "internalType": "string",
                "name": "picId",
                "type": "string"
            },
            {
                "indexed": false,
                "internalType": "string",
                "name": "picName",
                "type": "string"
            },
            {
                "indexed": false,
                "internalType": "uint256",
                "name": "timestamp",
                "type": "uint256"
            }
        ],
        "name": "PICRegistered",
        "type": "event"
    },
    {
        "inputs": [
            {
                "internalType": "string",
                "name": "_picId",
                "type": "string"
            },
            {
                "internalType": "string",
                "name": "_picName",
                "type": "string"
            }
        ],
        "name": "registerPIC",
        "outputs": [],
        "stateMutability": "nonpayable",
        "type": "function"
    }
];

let web3;
let agencyContract;
let account;

async function connectWallet() {
    if (window.ethereum) {
        web3 = new Web3(window.ethereum);
        try {
            const accounts = await window.ethereum.request({ method: 'eth_requestAccounts' });
            account = accounts[0];
            console.log("Connected account:", account);

            // Initialize contract
            agencyContract = new web3.eth.Contract(AGENCY_CONTRACT_ABI, AGENCY_CONTRACT_ADDRESS);

            alert("Wallet Connected: " + account);
            return true;
        } catch (error) {
            console.error("User denied account access", error);
            alert("Please allow access to your wallet.");
            return false;
        }
    } else {
        alert("Please install MetaMask!");
        return false;
    }
}

async function registerEntityOnBlockchain(event, formId) {
    event.preventDefault(); // Prevent default form submission

    const isConnected = await connectWallet();
    if (!isConnected) return;

    const picNameInput = document.getElementById('picName');
    const picName = picNameInput ? picNameInput.value : "Unknown Name";
    const picId = "PIC_" + Date.now(); // Generate a unique ID based on timestamp

    try {
        console.log("Registering PIC:", picId, picName);

        // Call the Smart Contract function
        // Note: sending to contract requires gas. Using 'send' method.
        await agencyContract.methods.registerPIC(picId, picName).send({ from: account })
            .on('transactionHash', function (hash) {
                console.log("Transaction Hash:", hash);

                // Set the hidden input field
                const hashField = document.getElementById('txHash');
                if (hashField) {
                    hashField.value = hash;
                } else {
                    console.error("Hidden field 'txHash' not found!");
                }

                // Submit the form immediately after getting hash
                // This allows the backend to proceed with PENDING state, while blockchain mines.
                document.getElementById(formId).submit();
            })
            .on('error', function (error, receipt) {
                console.error(error);
                alert("Transaction failed! Please check console.");
            });

    } catch (error) {
        console.error("Transaction Error", error);
        alert("Blockchain transaction failed or cancelled.");
    }
}
