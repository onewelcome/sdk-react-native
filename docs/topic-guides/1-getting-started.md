# Getting started

## Introduction

At the very beginning, you need to make sure that you have access to [Onegini SDK](https://docs.onegini.com/onegini-sdk.html). If you don't have a login and password please [contact us](https://www.onegini.com/en-us/about/contact-us).

## Add the plugin to your project

`npm install react-native-sdk-beta --save`
OR
`yarn add react-native-sdk-beta`

## Configure your project

The details about plugin configuration can be found in the [Configuration guide](./2-configuration.md).

## Initialize Onegini Flutter SDK

To start working with the plugin, we need to initialize Onegini SDK by calling `OneginiSdk.startClient` . You can pass additional config information here as an argument (ADD: reference to this method).

```
OneginiSdk.startClient()
    .then(() => console.log('Start succeed'))
    .catch(err => console.log('Start failed: ', err))
```
