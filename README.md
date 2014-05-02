bce-suite
=========

This is an implementation and infrastructure of the **Broadcast Encryption (BCE)**.

# Introduction

**Broadcast Encryption** is one of the public key encrytion schemes based on elliptic
curves, and it was also my research interest when I was in Soochow Univeristy.

Currently, this project contains:

* **libbcejni**: A C-based native library implementing the JNI (Java Native Interface) API for Boneh-Gentry-Waters BCE scheme.
* **bcejni**: A Java generic wrapper of **libbcejni**.
* **jbce**: A Java Object-Oriented encapsulation of **bcejni**.
* **bceserver**: A JavaEE web server for securely managing and distributing BCE private keys with Hibernate and Spring.

I am now working on the refactoring and continuous integration for this project bundle, and **MORE** stuff will come.


# References

For details about Broadcast Encryption, see:

* [Pairing Based Cryptography](http://crypto.stanford.edu/pbc/)
* [Pairing Based Cryptography for Broadcast Encryption](http://crypto.stanford.edu/pbc/bce/)
* [Boneh-Gentry-Waters broadcast encryption scheme](http://crypto.stanford.edu/~dabo/abstracts/broadcast.html)
