---
title: Autotask endpoint
keywords: 
last_updated: July 20, 2017
tags: []
summary: "Detailed description of the API of the Autotask endpoint."
---

## Overview

The Autotask endpoint allows to create, read, update and delete elements in Autotask.

Some of the features are:

- CRUD operations on any entity based
- Automatic detection of zone
- Conversion of XML to JSON and the other way around 
- Automatic conversion of fields based on fields definition on Autotask
- Polling to detect new records and updates in existing records

In order to make it easy to work with the endpoint, it does some automatic conversions so you don't need to worry
about data formats or XML. For example, in order to create a new account in Autotask you can do something like this:

```js
var accountId = app.endpoints.autotask.create('Account', {
    AccountName: '',
    AccountNumber: '123',
    AccountType: 2,
    Active: true,
    Address1: '62 test',
    AssertValue: 5834.46,
    CountryID: 12,
    Phone: '867-7362',
    OwnerResourceID: 30759324,
    UserDefinedFields: {
        LFNOTE: 'Test note!'
    }
});
```

As you can see you can just pass the JSON data and it will be converted automatically to the XML object requested by
the Autotask API. In order to know what's the data structure of the different entities in Autotask, you have to look
at their [API documentation](https://ww1.autotask.net/help/Content/AdminSetup/2ExtensionsIntegrations/APIs/APIs.htm).

You just need to remember a few rules:

- You need to follow the same fields structure as indicated in the Autotask API. Look at their docs to see details
  about fields' type, permissions, operations, etc.
- User defined fields go inside a field called `UserDefinedFields` (as shown in the sample above). You should ask your
  Autotask admin to know which are the available user defined fields.
- Date time fields are converted to milliseconds. When you build the JSON you can put a Date object and it will be
  converted automatically, but you will always see milliseconds coming from Autotask, which is very convenient to avoid
  time zone issues and you can easily parse them in SLINGR and Javascript.
- Numbers fields are passed and received as Javascript numbers. The endpoint will take care of the conversion.
- Boolean fields are passed and received as Javascript boolean. The endpoint will take care of the conversion.

## Quick start

You can create an account like this:

```js
var accountId = app.endpoints.autotask.create('Account', {
    AccountName: '',
    AccountNumber: '123',
    AccountType: 2,
    Active: true,
    Address1: '62 test',
    AssertValue: 5834.46,
    CountryID: 12,
    Phone: '867-7362',
    OwnerResourceID: 30759324,
    UserDefinedFields: {
        LFNOTE: 'Test note!'
    }
});
```

Read an account by `id`:

```js
var accounts = app.endpoints.autotask.query('Account', [
    {field: 'id', op: 'equals', value: accountId}
]);
accounts.forEach(function(account) {
    log(account.AccountName);
});
```

Read an account by `AccountName`:

```js
var accounts = app.endpoints.autotask.query('Account', [
    {field: 'AccountName', op: 'equals', value: 'Test Account 1'}
]);
accounts.forEach(function(account) {
    log(account.AccountName);
});
```

When there is a field that has some possible values (pick list), you can execute the following command in the console
to figure out the possible values (the endpoint must be deployed):

```js
var fieldInfo = app.endpoints.autotask.getEntityField('Account', 'AccountType');
fieldInfo.pickListValues.forEach(function(value) {
    log('label: '+value.label+', value: '+value.value);
});
```

## Configuration

You will need to create a user in Autotask that will be used to access the API. All requests to the API will be done
on behalf of that user.

### Username

The username of the user to make request to the API.

### Password

The password of the user to make request to the API.

### Polling enabled

Indicates if polling will be enabled to detect new records or updates in existing records.

### Polling frequency

How often (in minutes) polling will be done. It must be 1 or greater.

### Entities to poll

This is a comma-separated list of entities to poll. For example:

```
Account,Ticket,Task
```

Keep in mind that not all entities can be polled and some can only be polled to detect creations. Here is the list:

- Create and update: Account, AccountNote, AccountToDo, Contact, ContractNote, Phase, ProjectNote, Service, 
  ServiceBundle, ServiceCall, Task, TaskNote, Ticket, TicketNote, TimeEntry
- Only create: Appointment, ContractCost, ContractMilestone, InstalledProduct, Invoice, Opportunity, Project, 
  ProjectCost, PurchaseOrder, Quote, QuoteTemplate, TicketCost

## Javascript API

### Create

```js
var objectId = app.endpoints.autotask.create(entityName, data);
```

Creates an object for the given entity. You can check the available entities and their structure in the Autotask API
documentation.

This method returns the ID of the created object. If you need more details about it you need to query it.
 
Here is a sample to create an account:

```js
var accountId = app.endpoints.autotask.create('Account', {
    AccountName: '',
    AccountNumber: '123',
    AccountType: 2,
    Active: true,
    Address1: '62 test',
    AssertValue: 5834.46,
    CountryID: 12,
    Phone: '867-7362',
    OwnerResourceID: 30759324,
    UserDefinedFields: {
        LFNOTE: 'Test note!'
    }
});
log('account id: '+accountId);
```

### Query

```js
var objects = app.endpoints.autotask.query(entityName, filters);
```

Finds objects for the given entity where you can provide some filters. You can check the available entities in the 
Autotask API documentation. There you will also find how to define filters, which can be expressed in the endpoint like
this:

```js
var filters = [
    {field: 'fieldName', op: 'operationName', value: 'filterValue'}
];
```

So they are basically a list of objects where each of them defines a filter. Each filter has a `filter` field that
indicates the name of the field in the entity, `op` which is the operation name (check the Autotask API) and `value`
which is the value to filter by.

Please make sure you read the section `Query XML` in the Autotask API documentation to see all the options to create
queries.

The `query` method returns a list of objects that match the filters. Keep in mind that the Autotask API has a limit
of 1,000 objects per query. If you need more you need to paginate using the `id` as explained in the Autotask API
documentation.
 
Here is a sample to read accounts of type customer:

```js
var accounts = app.endpoints.autotask.query('Account', [
    {field: 'AccountType', op: 'equals', value: 1}
]);
accounts.forEach(function(account) {
    log(account.AccountName);
});
```

### Update

```js
var objectId = app.endpoints.autotask.update(entityName, data);
```

Updates an object for the given entity. You can check the available entities and their structure in the Autotask API
documentation.

This method returns the ID of the updated object. If you need more details about it you need to query it.
 
Here is a sample to update an existing account:

```js
var accounts = app.endpoints.autotask.query('Account', [{field: 'id', op: 'equals', value: 389902725}]);
var account = accounts[0];
account.accountName = 'New account name';
app.endpoints.autotask.update('Account', account);
```

One important thing is that the `id` must be present in the `data`, otherwise the update will fail.

### Delete

```js
app.endpoints.autotask.delete(entityName, objectId);
```

Delete an object for the given entity. You should check in the Autotask documentation which objects can be deleted
as this is available in just a few of them.

### Get information of entity

```js
var entityInfo = app.endpoints.autotask.getEntity(entityName);
```

Returns information about the given entity. The format of the response has this structure:

```js
{
  "name": "Account",
  "canCreate": true,
  "canUpdate": true,
  "canQuery": true,
  "canDelete": false,
  "hasUserDefinedFields": true,
  "userAccessForCreate": "All",
  "userAccessForUpdate": "All",
  "userAccessForQuery": "All",
  "userAccessForDelete": "None"
}
```

Please check Autotask API's documentation for more information about the meaning of those fields.

### Get information of entity fields

```js
var entityFieldsInfo = app.endpoints.autotask.getEntityFields(entityName);
```

Returns a list of of JSON with information for each field available in the entity. This method returns
all fields, including user defined ones.

This is the structure of the response:

```js
[
  {
    "name": "id",
    "label": "ID",
    "type": "long",
    "userDefinedField": false,
    "length": 0,
    "required": false,
    "readOnly": true,
    "queryable": false,
    "reference": false,
    "pickList": false
  },
  {
    "name": "AccountName",
    "label": "Account Name",
    "type": "string",
    "userDefinedField": false,
    "length": 100,
    "required": true,
    "readOnly": false,
    "queryable": true,
    "reference": false,
    "pickList": false
  },
  {
    "name": "OwnerResourceID",
    "label": "Account Owner",
    "type": "integer",
    "userDefinedField": false,
    "length": 0,
    "required": true,
    "readOnly": false,
    "queryable": true,
    "reference": true,
    "referenceEntityType": "Resource",
    "pickList": false
  },
  ...
  {
    "name": "AccountType",
    "label": "Account Type",
    "type": "short",
    "userDefinedField": false,
    "length": 0,
    "required": true,
    "readOnly": false,
    "queryable": true,
    "reference": false,
    "pickList": true,
    "pickListValues": [
      {
        "value": 1,
        "label": "Customer",
        "defaultValue": false,
        "sortOrder": 1,
        "active": true,
        "system": true
      },
      {
        "value": 2,
        "label": "Lead",
        "defaultValue": false,
        "sortOrder": 2,
        "active": true,
        "system": true
      },
      {
        "value": 3,
        "label": "Prospect",
        "defaultValue": false,
        "sortOrder": 3,
        "active": true,
        "system": true
      },
      {
        "value": 4,
        "label": "Dead",
        "defaultValue": false,
        "sortOrder": 4,
        "active": true,
        "system": true
      },
      {
        "value": 6,
        "label": "Cancellation",
        "defaultValue": false,
        "sortOrder": 6,
        "active": true,
        "system": true
      },
      {
        "value": 7,
        "label": "Vendor",
        "defaultValue": false,
        "sortOrder": 7,
        "active": true,
        "system": true
      },
      {
        "value": 8,
        "label": "Partner",
        "defaultValue": false,
        "sortOrder": 8,
        "active": true,
        "system": true
      }
    ]
  }
]
```

If you know the name of the field you want to get more information, you can use this method:

```js
var fieldInfo = app.endpoints.autotask.getEntityField(entityName, fieldName);
```

This method will return the JSON of the requested field only.

### Get web URL

```js
var webUrl = app.endpoints.autotask.getWebUrl();
```

Returns the base web URL of the Autotask account configured in the endpoint. This is useful if you need to build
links to pages in Autotask. However you should be careful as URLs aren't part of the API of Autotask and you should
maintain those links manually.

## Events

### Record change

This event is triggered when a record is created or updated in Autotask (only when polling is enabled). The structure
of the event is the following:

```js
{
    entityType: 'entityName',
    record: { ... }
}
```

You will find the name of the entity in `entityType`, like `Account` or `Ticket`, and the actual record inside `record`.

## About SLINGR

SLINGR is a low-code rapid application development platform that accelerates development, with robust architecture for integrations and executing custom workflows and automation.

[More info about SLINGR](https://slingr.io)

## License

This endpoint is licensed under the Apache License 2.0. See the `LICENSE` file for more details.
