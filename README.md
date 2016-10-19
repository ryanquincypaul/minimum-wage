# minimum-wage

A Clojure powered RESTful web service providing State and Federal minimum wage data.

## Example

```
curl http://fairpay.azurewebsites.net/minimum-wage/years/2016/states/mi
```

Output

```JSON
{
  "year": "2016",
  "state": "Michigan",
  "postalcode": "MI",
  "minimum_wage": "8.50",
  "url": "/years/2016/states/mi",
  "year_url": "/years/2016",
  "federal_wage_info_url": "/years/2016/federal"
}
```

## Usage

This service can be consumed by anything that is set up to handle JSON responses. Included in each response are URLs that can be used to explore the available minimum wage data. 

## Motivation

I wrote this as a microservice to a future web application. I could not find a publicly available API with minimum wage information. This is just a compilation of that information with an API to access it. 

## API Reference

A description of all the calls can be found on the [wiki](https://github.com/ryanquincypaul/minimum-wage/wiki).

## Install

Pull down the project locally and run `lein deps` and then `lein ring uberwar` to generate a WAR file that you can host on your Java server of choice.

## Prerequisites

You will need [Leiningen][] 2.0.0 or above installed.

[leiningen]: https://github.com/technomancy/leiningen

## Configuration

By default, the data source will be the CSV files found in the resources directory. The files below will always be available as they are built into the WAR.
```
resources/wage_data/dev/state_minimum_wage.csv
resources/wage_data/dev/federal_minimum_wage.csv
```

Once deployed, updated CSV files with the same name can be put in the `resources\wage_data` folder on the server to update the wage data instead of rebuilding the WAR.

## Test

Run `lein test` in the main project folder.

## TODO

I hope to add/fix the following
* Add local minimum wage information
* Nicer home page
* Prune needless URL attributes from responses (e.g. when there are no federal wages for a year)

## Acknowledgements

* National Employment Law Project, author of this [handy list](http://www.raisetheminimumwage.com/pages/minimum-wage-state)

## Contributions

If you are interesting in contributing to this or any other of my projects, contact me [here](mailto:ryan.quincy.paul@gmail.com)

## License

Copyright (C) 2016 Ryan Paul

Distributed under the Eclipse Public License, the same as Clojure.
