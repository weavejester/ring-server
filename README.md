# Ring-Server

A library for starting a web server to serve a [Ring][1] handler with
sensible default options and environment variable overrides.

[1]: https://github.com/mmcgrana/ring

## Features

When starting in development mode (i.e. `LEIN_NO_DEV` is not set):

* The server finds a free port to start on
* It automatically reloads changed files
* It renders exceptions and their stacktraces in HTML
* A web browser is automatically opened to the started server

In production:

* You can specify the port via the `PORT` environment variable
* You can add hooks to run on startup and shutdown.

## Install

Add the following dependency to your `project.clj` file:

    [ring-server "0.2.1"]

## Usage 

Simple usage:

```clojure
(use 'ring.server.standalone)
(serve your-handler)
```

You can also specify a map of options:

```clojure
(serve your-handler {:port 4040})
```

The following options are supported:

* `:port`    - The port to start the server on, overrides `$PORT`

* `:join?`   - Whether to wait until the server stops (default true)

* `:init`    - A function executed when the server starts

* `:destroy` - A function executed when the server stops

* `:open-browser?` -
  True if you want a browser to be opened to the server. Defaults to
  true in development mode, false in production mode.

* `:middleware` -
  A list of middleware functions to apply to the handler. Defaults to
  `[wrap-stacktrace wrap-reload]` in development.

## License

Copyright (C) 2012 James Reeves

Distributed under the Eclipse Public License, the same as Clojure.
