# zTron

**zTron** is a lightweight Java library for building and verifying Merkle trees — useful for ensuring **file integrity** and validating data authenticity.

## 🚀 Features

- Builds Merkle trees from input data
- Verifies Merkle proofs
- Designed to be simple and easy to use
- Suitable for integrity checks in storage or distributed systems

## 💡 What is a Merkle Tree?

A **Merkle tree** is a hash-based binary tree where:

- Leaf nodes represent data hashes
- Internal nodes represent combined hashes of their children

This structure makes it fast to detect tampering and verify that data hasn’t been altered.

## 🛠️ Build & Test

This is a Maven project. To build and test:

```bash
# clone the repo
git clone https://github.com/zstoimchev/ztron.git

# navigate into the project
cd ztron

# build the project
mvn clean package

# run tests
mvn test
