// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

contract AgencyRegistry {
    event PICRegistered(string indexed picId, string picName, uint256 timestamp);

    // Cheap function: Only emits an event, does not store data in state.
    // Cost: ~22,000 - 30,000 Gas (Very cheap)
    function registerPIC(string memory _picId, string memory _picName) public {
        emit PICRegistered(_picId, _picName, block.timestamp);
    }
}
