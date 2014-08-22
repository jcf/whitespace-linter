(ns whitespace-linter.check)

(def max-line-width 80)

;; Line {:number 234 :content "Actually string on this line"}
(defn hard-tabs [lines]
  {:pre [(seq lines)]}
  (filter #(.contains (:content %) "\t") lines))

(defn trailing-whitespace [lines]
  {:pre [(seq lines)]}
  (filter #(re-matches #".*\s+$" (:content %)) lines))

(defn long-lines [lines]
  {:pre [(seq lines)]}
  (let [line-length #(-> % :content count)]
    (filter #(> (line-length %) max-line-width) lines)))

;; File {:path "foobar.clj" :content "File contents\n"}
(defn missing-final-newline? [{:keys [content]}]
  {:pre [(string? content)]}
  (not (.endsWith content "\n")))

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
