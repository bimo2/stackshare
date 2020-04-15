# stackshare

_stackshare_ is a predictive web system built in Scala for collecting and aggregating tech stack data on developers and companies using job listings. The application fetches and parses job listings from webpages and evaluates the text content to estimate the languages (JS, Scala, Swift...) related to the application. Using position and developer data as predicates, _stackshare_ shares dashboards profiling company tech stacks, language popularity and developer/position suggestions. _stackshare_ is a simple application based on [stackshare.io](https://stackshare.io/).

### System Requirements

- `scala: ^2.13.1`
- `sbt: ^1.3.9`
- `java: ^11.0.6`
- `mongodb: ^4.2.0`

## Build

```zsh
# Start MongoDB on http://localhost:27017
mongod --dbpath /usr/local/var/mongodb --logpath /usr/local/var/log/mongodb/mongo.log --fork

# Compile and run Play project on http://localhost:9000
sbt compile
sbt run
```
