# Getting started

## Introduction

At the very beginning, you need to make sure that you have access to [Onegini SDK](https://docs.onegini.com/onegini-sdk.html). If you don't have a login and password please [contact us](https://www.onegini.com/en-us/about/contact-us).

## Add the plugin to your project

`npm install onegini-react-native-sdk --save`
OR
`yarn add onegini-react-native-sdk`

## Configure your project

The details about plugin configuration can be found in the [Configuration guide](./2-configuration.md).

## Initialize Onegini React Native SDK

To start working with the plugin, we need to initialize Onegini SDK by calling [startClient](../reference-guides/startClient.md) . You can pass additional config information here as an argument (see [Config](../reference-guides/Config.md)).

```
OneginiSdk.startClient()
    .then(() => console.log('Start succeed'))
    .catch(error => console.log('Start failed: ', error.message))
```
