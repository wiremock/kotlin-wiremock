package com.marcinziolo.kotlin.wiremock

import com.github.tomakehurst.wiremock.client.CountMatchingMode
import com.github.tomakehurst.wiremock.client.CountMatchingStrategy
import com.github.tomakehurst.wiremock.client.CountMatchingStrategy.*
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.http.RequestMethod
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder

class VerificationDsl {


    enum class MatchMode {
        EQUAL_TO,
        PATTERN,
        PATH_EQUAL_TO,
        PATH_PATTERN,
        ANY,
    }

    class Verification(
            var count: CountMatchingStrategy,
            val requestBuilder: RequestPatternBuilder,
            var block: RequestPatternBuilder.() -> Unit,
    ) {

    }

    data class VerifyDsl(
            internal val verifications: MutableList<Verification> = mutableListOf()
    ) {
        fun addVerification(url: String = "", match: MatchMode = MatchMode.EQUAL_TO,
                            atLeast:Int? = null, atMost:Int? = null, exactly:Int?=null,
                            method: RequestMethod, block: RequestPatternBuilder.() -> Unit) {
            val urlPattern = when (match) {
                MatchMode.EQUAL_TO -> WireMock.urlEqualTo(url)
                MatchMode.PATTERN -> WireMock.urlMatching(url)
                MatchMode.PATH_EQUAL_TO -> WireMock.urlPathEqualTo(url)
                MatchMode.PATH_PATTERN -> WireMock.urlPathMatching(url)
                MatchMode.ANY -> WireMock.anyUrl()
            }

            val count = when {
                exactly != null -> CountMatchingStrategy(EQUAL_TO, exactly)
                atLeast != null && atMost != null -> CountingStrategyWith(atLeast, atMost)
                atLeast != null -> CountMatchingStrategy(GREATER_THAN_OR_EQUAL, atLeast)
                atMost != null -> CountMatchingStrategy(LESS_THAN_OR_EQUAL, atMost)
                else -> CountMatchingStrategy(GREATER_THAN_OR_EQUAL, 1)
            }

            verifications.add(Verification(count, RequestPatternBuilder(method, urlPattern), block))
        }

        class CountingStrategyWith(private val atLeast: Int, private val atMost: Int):
                CountMatchingStrategy(CountWithin(atLeast, atMost), atLeast){
            init {
                if(atLeast > atMost) {
                    throw IllegalArgumentException("In verification 'atLeast' cannot be greater than 'atMost' (atLeast=$atLeast atMost=$atMost)")
                }
            }

            override fun match(actual: Int): Boolean {
                return actual in atLeast..atMost
            }
        }

        class CountWithin(private val atLeast: Int, private val atMost: Int) : CountMatchingMode {
            override fun test(actual: Int?, expected: Int?): Boolean {
                error("Not used. Use CountingStrategyWith instead")
            }

            override fun getFriendlyName()= "Within ($atLeast..$atMost)"

        }

        fun get(url: String = "/.*", match: MatchMode = MatchMode.PATTERN,
                atLeast:Int? = null, atMost:Int? = null, exactly:Int?=null,
                block: RequestPatternBuilder.() -> Unit = {}) {
            addVerification(url, match, atLeast, atMost, exactly, RequestMethod.GET, block)
        }

        fun post(url: String = "", match: MatchMode = MatchMode.EQUAL_TO,
                 atLeast:Int? = null, atMost:Int? = null, exactly:Int?=null,
                 block: RequestPatternBuilder.() -> Unit) {
            addVerification(url, match, atLeast, atMost, exactly, RequestMethod.POST, block)
        }

        fun put(url: String = "", match: MatchMode = MatchMode.EQUAL_TO,
                atLeast:Int? = null, atMost:Int? = null, exactly:Int?=null,
                block: RequestPatternBuilder.() -> Unit) {
            addVerification(url, match, atLeast, atMost, exactly, RequestMethod.PUT, block)
        }

        fun delete(url: String = "", match: MatchMode = MatchMode.EQUAL_TO,
                   atLeast:Int? = null, atMost:Int? = null, exactly:Int?=null,
                   block: RequestPatternBuilder.() -> Unit) {
            addVerification(url, match, atLeast, atMost, exactly, RequestMethod.DELETE, block)
        }

        fun head(url: String = "", match: MatchMode = MatchMode.EQUAL_TO,
                 atLeast:Int? = null, atMost:Int? = null, exactly:Int?=null,
                 block: RequestPatternBuilder.() -> Unit) {
            addVerification(url, match, atLeast, atMost, exactly, RequestMethod.HEAD, block)
        }

        fun options(url: String = "", match: MatchMode = MatchMode.EQUAL_TO,
                    atLeast:Int? = null, atMost:Int? = null, exactly:Int?=null,
                    block: RequestPatternBuilder.() -> Unit) {
            addVerification(url, match, atLeast, atMost, exactly, RequestMethod.OPTIONS, block)
        }

        fun patch(url: String = "", match: MatchMode = MatchMode.EQUAL_TO,
                  atLeast:Int? = null, atMost:Int? = null, exactly:Int?=null,
                  block: RequestPatternBuilder.() -> Unit) {
            addVerification(url, match, atLeast, atMost, exactly, RequestMethod.PATCH, block)
        }

        fun trace(url: String = "", match: MatchMode = MatchMode.EQUAL_TO,
                  atLeast:Int? = null, atMost:Int? = null, exactly:Int?=null,
                  block: RequestPatternBuilder.() -> Unit) {
            addVerification(url, match, atLeast, atMost, exactly, RequestMethod.TRACE, block)
        }

        fun any(url: String = "", match: MatchMode = MatchMode.EQUAL_TO,
                atLeast:Int? = null, atMost:Int? = null, exactly:Int?=null,
                block: RequestPatternBuilder.() -> Unit) {
            addVerification(url, match, atLeast, atMost, exactly, RequestMethod.ANY, block)
        }
    }



}
