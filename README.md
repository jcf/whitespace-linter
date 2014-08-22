# whitespace-linter [![Build Status](https://travis-ci.org/listora/whitespace-linter.svg?branch=master)](https://travis-ci.org/listora/whitespace-linter)

A Leiningen plugin to find the invisible errors you didn't even know
existed.

## Usage

Add to your `:plugins`, most likely in your application's `:dev`
profile.

``` clj
[whitespace-linter "0.1.0"]
```

To lint all files in your `:source-paths` and `:test-paths` run with
no arguments.

``` sh
lein whitespace-linter
```

You can also specify the list of files to lint if you want to check
files that aren't in your classpath.

``` sh
lein whitespace-linter app/{scripts,styles}/**/*
```

## License

Copyright © 2014 Listora

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
