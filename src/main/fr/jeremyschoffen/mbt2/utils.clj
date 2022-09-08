(ns fr.jeremyschoffen.mbt2.utils)

(defn transfer-doc* [src dest]
  (let [doc (-> src resolve meta (select-keys [:doc :arglists]))]
    (-> dest
        resolve
        (alter-meta! merge doc))))

(defmacro transfer-doc [src dest]
  `(transfer-doc* ~src ~dest))
