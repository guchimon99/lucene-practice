# 環境

開発した環境

```
% java --version
java 11.0.5 2019-10-15 LTS
Java(TM) SE Runtime Environment 18.9 (build 11.0.5+10-LTS)
Java HotSpot(TM) 64-Bit Server VM 18.9 (build 11.0.5+10-LTS, mixed mode)
% gradle --version

------------------------------------------------------------
Gradle 8.2.1
------------------------------------------------------------

Build time:   2023-07-10 12:12:35 UTC
Revision:     a38ec64d3c4612da9083cc506a1ccb212afeecaa

Kotlin:       1.8.20
Groovy:       3.0.17
Ant:          Apache Ant(TM) version 1.10.13 compiled on January 4 2023
JVM:          11.0.5 (Oracle Corporation 11.0.5+10-LTS)
OS:           Mac OS X 10.16 x86_64
```

# ビルド

```
./gradlew build
```

# 実行

犬は検索文字列
terms.txtに作成された Term の一覧が出力される

## 日本語アナライザ

```
java -jar ./app/build/libs/app.jar 犬 JAPANESE
```

## 日本語Nグラムアナライザ

```
java -jar ./app/build/libs/app.jar 犬 JAPANESE_NGRAM
```
