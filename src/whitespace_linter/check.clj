(ns whitespace-linter.check
  "Provides file and line-based checks.

  Line-based check functions are passed a collection of hash-maps, one
  per line. Each hash-map contains the :path to the file, :line
  number, and :content of the line. For example,

      [{:path #<File example/file.txt>
        :line 1
        :content \"Content of the line, warts and all\"}

  File-based check functions are passed a hash-map of the :path to the
  file, and its :content. For example,

      {:path #<File example/file.txt>
       :content \"Content of the line, warts and all\"}

  To indicate an error has been found in a line-based checker return
  the hash-map for the erroneous line. To indicate an issue with a
  file-based checker return a truthy value.

  When adding a new check, don't forget to add a report-friendly name
  to `humanized-check-names`.

  Checkers are run via `whitespace-linter.lint/check-file` and
  `whitespace-linter.lint/check-lines`."
  (:require [clojure.string :as str]))

(def ^:const max-line-width
  "This is a universal constant, right?"
  80)

(defn hard-tabs [args lines]
  {:pre [(seq lines)]}
  {:errors (filter #(.contains (:content %) "\t") lines)})

(defn trailing-whitespace [args lines]
  {:pre [(seq lines)]}
  {:errors (filter #(re-matches #".*\s+$" (:content %)) lines)})

(defn long-lines
  [{warn-line-width :warn-line-width
    error-line-width :error-line-width
    :or {warn-line-width Long/MAX_VALUE
         error-line-width max-line-width}
    :as args} lines]
  {:pre [(seq lines)]}
  (let [line-length #(-> % :content str/trim-newline count)]
    {:warnings (filter #(and (> (line-length %) warn-line-width)
                             (<= (line-length %) error-line-width)) lines)
     :errors (filter #(> (line-length %) error-line-width) lines)}))

;; File {:path "foobar.clj" :content "File contents\n"}
(defn missing-final-newline? [args {:keys [content]}]
  {:pre [(string? content)]}
  {:errors (not (.endsWith content "\n"))})

(def humanized-check-names
  {:hard-tabs "Hard tabs"
   :trailing-whitespace "Trailing whitespace"
   :long-lines "Long lines"
   :missing-final-newline "Missing final newline"})

(def line-checks
  "Collection of checks to run on each file."
  {:hard-tabs hard-tabs
   :trailing-whitespace trailing-whitespace
   :long-lines long-lines})

(def file-checks
  {:missing-final-newline missing-final-newline?})
