# minimum-wage

A Clojure powered RESTful web service providing State and Federal minimum wage data.

## Project Site

More info and the code can be found [here](https://github.com/ryanquincypaul/minimum-wage).

## Table-of-Contents

* [Years](#years)
* [Year](#year)
* [States](#states)
* [State](#state)
* [Federal](#federal)

## Preface

All of these calls must be appended to the end of the website hosting the service. Currently that is...

`http://fairpay.azurewebsites.net/minimum-wage`

## <a name="years"></a>Years

Lists the available years that contain either State or Federal minimum wage data.

`/years`

### Response

```JSON
{
  "years": [{
    "year": "2015",
    "url": "/years/2015",
    "states_url": "/years/2015/states",
    "federal_wage_info_url": "/years/2015/federal"
  }],
  "url": "/years"
}
```

## <a name="year"></a>Year

Gets URLs to navigate data in a given year.

`/years/:year`

### Response

```JSON
{
  "year": "2016",
  "url": "/years/2016",
  "states_url": "/years/2016/states",
  "federal_wage_info_url": "/years/2016/federal",
  "all_years_url": "/years"
}
```
## <a name="states"></a>States

Lists all the states that contain wage data for a year.

`/years/:year/states`

### Response

```JSON
{
  "year": "2016",
  "states": [{
    "state": "Michigan",
    "postalcode": "MI",
    "url": "/years/2016/states/mi"
  }, {
    "state": "Minnesota",
    "postalcode": "MN",
    "url": "/years/2016/states/mn"
  }, {
    "state": "Mississippi",
    "postalcode": "MS",
    "url": "/years/2016/states/ms"
  }],
  "url": "/years/2016/states",
  "year_url": "/years/2016",
  "federal_wage_info_url": "/years/2016/federal"
}
```

## <a name="state"></a>State

Gets wage information for a state in a given year.

`/years/:year/states/:state-postal-abbreviation`

### Response

```JSON
{
  "year": "2016",
  "state": "Michigan",
  "postalcode": "MI",
  "minimum-wage": "8.50",
  "url": "/years/2016/states/mi",
  "year_url": "/years/2016",
  "federal_wage_info_url": "/years/2016/federal"
}
```

## <a name="federal"></a>Federal

Gets federal wage data for a given year.

`/years/:year/federal`

### Response

```JSON
{
  "year": "2016",
  "minimum-wage": "7.25",
  "url": "/years/2016/federal",
  "states_url": "/years/2016/states",
  "year_url": "/years/2016"
}
```
