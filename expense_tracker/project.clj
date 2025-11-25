(defproject expense_tracker "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [com.github.seancorfield/next.jdbc "1.3.1070"]
                 [org.postgresql/postgresql "42.7.8"]
                 [com.github.seancorfield/honeysql "2.2.891"]
                 [org.clojure/tools.cli "1.2.245"]
                 [compojure "1.7.2"]
                 [cheshire "5.12.0"]
                 [ring/ring-core "1.15.3"]
                 [ring/ring-json "0.5.1"]
                 [ring/ring-jetty-adapter "1.15.3"]]
  :main ^:skip-aot expense-tracker.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
