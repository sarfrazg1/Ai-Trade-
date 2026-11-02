package com.example.data.repository

import com.example.domain.model.ConceptCategory
import com.example.domain.model.FinancialConcept

object FinancialLearningCenterData {

    val allConcepts: List<FinancialConcept> by lazy {
        economicConcepts + cryptoConcepts
    }

    val economicConcepts: List<FinancialConcept> = listOf(
        FinancialConcept(
            id = "cpi",
            category = ConceptCategory.ECONOMIC_CONCEPTS,
            term = "CPI",
            fullForm = "Consumer Price Index (Sarif Ke Akhrajat Ka Index)",
            simpleUrduMeaning = "CPI aik aisa number hota hai jo batata hai ke aam cheezon ki qeemat pehle ke muqable mein kitni barh gayi ya kam hui hai.",
            everydayExample = "Agar pehle 100 rupay mein doodh aur roti milti thi aur ab wohi cheezen 110 rupay mein mil rahi hain, to iska matlab hai ke mehngai (CPI) 10% barh gayi.",
            whyItMatters = "Hukoomat aur central bank is number ko dekh kar faisla karte hain ke mulk mein mehngai qaboo mein hai ya begar rahi hai.",
            marketImpactExplanation = "Agar America mein CPI tawaqqo se zyada aa jaye, to Federal Reserve sood (interest rate) barha sakta hai. Is se Dollar strong hota hai aur Bitcoin aur Stocks par girawat ka dabao aa sakta hai.",
            whatTraderShouldWatch = "Har mahine aane wali CPI report. Agar Actual number Forecast se zyada aaye to market mein foran jhatka lagta hai."
        ),
        FinancialConcept(
            id = "ppi",
            category = ConceptCategory.ECONOMIC_CONCEPTS,
            term = "PPI",
            fullForm = "Producer Price Index (Factory aur Karkhanon Ka Index)",
            simpleUrduMeaning = "PPI batata hai ke factories aur karobaar ko kacha maal banane par kitni lagat aa rahi hai.",
            everydayExample = "Agar bakery wale ko maida aur cheeni mehengi milegi, to aage chal kar roti aur cake bhi mehnga hi bechega.",
            whyItMatters = "PPI aane wale kal ki mehngai ka paish-khema hota hai. Jab karkhane mehanga maal banayenge to aam awam ke liye CPI bhi barh jayega.",
            marketImpactExplanation = "Zyada PPI ka matlab aage CPI bhi barhega. Is se interest rates barhne ka khauf peda hota hai jo crypto ke liye aam tor par manfi (bearish) hota hai.",
            whatTraderShouldWatch = "PPI number ka CPI se taluq. Agar dono barh rahe hon to central bank sakhti karta hai."
        ),
        FinancialConcept(
            id = "fed",
            category = ConceptCategory.ECONOMIC_CONCEPTS,
            term = "Fed",
            fullForm = "Federal Reserve (America Ka Markazi Bank)",
            simpleUrduMeaning = "America ka sab se bara sarkari bank jo poori dunya ke dollar aur sood ko control karta hai, jaise Pakistan mein State Bank.",
            everydayExample = "Jaise ghar ka sarbarah faisla karta hai ke kitna kharch karna hai aur kitna bachana hai, waise hi Fed poori dunya ke karobari paison ki gardish tay karta hai.",
            whyItMatters = "Poori dunya mein dollar chalta hai. Fed jo bhi faisla kare, uski goonj har mulk ki currency, petrol aur Bitcoin tak aati hai.",
            marketImpactExplanation = "Fed agar meherban ho to saste qarz deta hai aur Bitcoin asman ko chhoota hai. Fed agar ghusse mein aaye to market gir jati hai.",
            whatTraderShouldWatch = "Fed Chairman (Jerome Powell) ki taqreerain aur unka lahja (Hawkish ya Dovish)."
        ),
        FinancialConcept(
            id = "fomc",
            category = ConceptCategory.ECONOMIC_CONCEPTS,
            term = "FOMC",
            fullForm = "Federal Open Market Committee",
            simpleUrduMeaning = "Fed ke 12 ahem afraad ki committee jo saal mein 8 martaba meeting karke sood barhane ya girane ka aakhri faisla karti hai.",
            everydayExample = "Jaise mohallay ki panchayat ya company ka board of directors mil kar bara faisla kare.",
            whyItMatters = "FOMC meeting ke din poori dunya ke traders saansein rok kar baithte hain kyunki yeh meeting market ki disha badal sakti hai.",
            marketImpactExplanation = "Meeting ke aakhri ghante mein jab statement aati hai to Bitcoin $1,000 se $3,000 tak foran ooper ya neeche chhalang laga sakta hai.",
            whatTraderShouldWatch = "FOMC Rate Decision aur uske 30 minute baad hone wali Press Conference."
        ),
        FinancialConcept(
            id = "interest_rates",
            category = ConceptCategory.ECONOMIC_CONCEPTS,
            term = "Interest Rates",
            fullForm = "Soodi Sharah (Qarz Par Munafe Ka Rate)",
            simpleUrduMeaning = "Woh sharah jo bank doosre baron ya logon ko qarz dete waqt wasool karte hain ya deposit par dete hain.",
            everydayExample = "Agar aap bank se 100 rupay lein aur wapis 105 dene paren to 5% sood hai. Agar bank kahe ab 110 dene honge to qarz lena mushkil ho jayega.",
            whyItMatters = "Jab sood zyada hota hai to log karobar aur crypto se paisa nikal kar bank mein mehfooz munafa lene chale jate hain.",
            marketImpactExplanation = "High Interest Rates = Crypto aur Stocks ke liye sakhti (Bearish). Rate Cuts (Sood me kami) = Crypto ke liye liquidity ka toofan (Bullish).",
            whatTraderShouldWatch = "Fed kab sood kam karne ka ailan karta hai. Rate cuts aam tor par bull market ka aaghaz karti hain."
        ),
        FinancialConcept(
            id = "inflation",
            category = ConceptCategory.ECONOMIC_CONCEPTS,
            term = "Inflation",
            fullForm = "Mehngai (Qeematon Ka Barhna)",
            simpleUrduMeaning = "Paison ki qadar kam hona aur cheezon ka musalsal mehnga hote chale jana.",
            everydayExample = "Pehle 1000 rupay mein poora rashan ka thela bhar jata tha, ab usi thele ke liye 3000 rupay chahiye.",
            whyItMatters = "Zyada mehngai awam ki bachat khatam kar deti hai aur mulk ka maashi pahiya jam kar deti hai.",
            marketImpactExplanation = "Halka inflation (2%) achha hota hai. Bohat zyada inflation central bank ko sood barhane par majboor karta hai jo assets ko crash kar sakta hai.",
            whatTraderShouldWatch = "Salana Inflation Rate. Target 2.0% hota hai, agar 3% ya 4% se ooper rahe to khatray ki ghanti hai."
        ),
        FinancialConcept(
            id = "deflation",
            category = ConceptCategory.ECONOMIC_CONCEPTS,
            term = "Deflation",
            fullForm = "Qeematon Ka Girna aur Mandi",
            simpleUrduMeaning = "Cheezon ki qeematon ka girna kyunki logon ke paas khareedne ke liye paisa hi nahi bacha.",
            everydayExample = "Dukaan par cheezen sasti ho rahi hon lekin log dukaan par na jayen kyunki unki mulazmat chhut gayi hai.",
            whyItMatters = "Deflation sunne mein achhi lagti hai lekin yeh shadeed maashi beemari aur berozgari ki alamat hoti hai.",
            marketImpactExplanation = "Deflation ke doran log kharch rokk dete hain, companiyon ke munafe khatam hote hain aur tamam risky markets dabao mein aati hain.",
            whatTraderShouldWatch = "Consumer demand aur retail sales data."
        ),
        FinancialConcept(
            id = "core_cpi",
            category = ConceptCategory.ECONOMIC_CONCEPTS,
            term = "Core CPI",
            fullForm = "Bunyadi Mehngai (Food aur Energy ke baghair)",
            simpleUrduMeaning = "Aisi mehngai jismein khorak aur petrol/gas ki qeematon ko nikaal kar dekha jata hai kyunki yeh mosam aur jang se foran badalti hain.",
            everydayExample = "Kharboozay aur petrol ki qeemat to roz badalti hai, lekin kapron, kirayon aur ilaj ke kharchay aahista badalte hain. Core CPI inhi paki cheezon ko naapti hai.",
            whyItMatters = "Fed aam CPI se zyada Core CPI par yaqeen karta hai kyunki yeh asli mehngai ka rujhan batati hai.",
            marketImpactExplanation = "Agar Core CPI barh rahi ho to samjhein mehngai jarr pakar chuki hai aur Fed sakhti karega.",
            whatTraderShouldWatch = "Core CPI Month-on-Month aur Year-on-Year numbers."
        ),
        FinancialConcept(
            id = "pce",
            category = ConceptCategory.ECONOMIC_CONCEPTS,
            term = "PCE",
            fullForm = "Personal Consumption Expenditures",
            simpleUrduMeaning = "Aam shehriyon ki jeb se hone wale kharchon ka hisab jo Fed ka sab se pasandeeda paimana hai.",
            everydayExample = "CPI sirf fix thela naapti hai, jabke PCE dekhti hai ke agar goosht mehnga hua to kya logon ne murghi khareedna shuru ki ya nahi.",
            whyItMatters = "Federal Reserve ka rasmi 2% mehngai ka hadaf dar-asl PCE par mabni hota hai.",
            marketImpactExplanation = "PCE number tawaqqo se kam aane par market mein foran zabardast tezi (rally) aati hai.",
            whatTraderShouldWatch = "Har mahine ke aakhir mein aane wala Core PCE Price Index."
        ),
        FinancialConcept(
            id = "gdp",
            category = ConceptCategory.ECONOMIC_CONCEPTS,
            term = "GDP",
            fullForm = "Gross Domestic Product (Kul Mulki Paidawar)",
            simpleUrduMeaning = "Aik saal ya teen mahine mein kisi mulk ke andar peda hone wali tamam cheezon aur khidmaat ki kul maali qeemat.",
            everydayExample = "Aik dukan ka poore saal ka kul bikri aur karobar ka hisab.",
            whyItMatters = "GDP barhne ka matlab hai mulk taraqqi kar raha hai, logon ko mulazmat mil rahi hai aur factories chal rahi hain.",
            marketImpactExplanation = "Mazboot GDP karobar ke liye achhi hai lekin agar had se zyada tez ho to mehngai barhati hai. Manfi GDP recession ka ailan karti hai.",
            whatTraderShouldWatch = "US Quarterly GDP growth rate (2% se 3% standard sehat-mand maeeshat mani jati hai)."
        ),
        FinancialConcept(
            id = "nfp",
            category = ConceptCategory.ECONOMIC_CONCEPTS,
            term = "NFP",
            fullForm = "Nonfarm Payrolls (Ziraat ke ilawa nayi mulazmatain)",
            simpleUrduMeaning = "America mein pichle mahine kisanon ke ilawa kitne logon ko nayi jobs mileen.",
            everydayExample = "Shehar ki companiyon ne kitne naye mulazmeen bharti kiye.",
            whyItMatters = "Har mahine ke pehle Jummay (Friday) ko aane wali dunya ki sab se zyada halchal machane wali report.",
            marketImpactExplanation = "Agar NFP bohat zyada aaye to maeeshat garam hai aur Fed sood kam nahi karega (Bearish for crypto). Agar NFP kam aaye to rate cut ke imkaanat barhte hain.",
            whatTraderShouldWatch = "Har mahine ke pehle Jummay sham 5:30/6:30 PM (PKT) par NFP release."
        ),
        FinancialConcept(
            id = "unemployment_rate",
            category = ConceptCategory.ECONOMIC_CONCEPTS,
            term = "Unemployment Rate",
            fullForm = "Berozgari Ki Sharah",
            simpleUrduMeaning = "Mulk ke kitne pratishat log kaam dhoond rahe hain lekin unhein mulazmat nahi mil rahi.",
            everydayExample = "Agar 100 mein se 4 logon ke paas naukri na ho to 4% berozgari hai.",
            whyItMatters = "Berozgari barhne ka matlab awam ke paas kharch karne ko paise nahi hain.",
            marketImpactExplanation = "Agar berozgari 4.3% ya 4.5% se ooper nikal jaye to Fed par maeeshat bachane ke liye sood girane ka shadeed dabao aa jata hai.",
            whatTraderShouldWatch = "Sahm Rule indicator jo berozgari barhne par foran recession ki nishandahi karta hai."
        ),
        FinancialConcept(
            id = "treasury_yields",
            category = ConceptCategory.ECONOMIC_CONCEPTS,
            term = "Treasury Yields",
            fullForm = "Americi Sarkari Bonds Ka Munafa (US 10Y Yield)",
            simpleUrduMeaning = "Jab log Americi hukoomat ko qarz dete hain to hukoomat unhein saalana kitna pakka munafa ada karti hai.",
            everydayExample = "Pakistan mein National Savings Certificate ya Prize Bond par milne wala pakka munafe ka rate.",
            whyItMatters = "Dunya ka sab se mehfooz tareen sarmaya Americi Treasury bond samjha jata hai.",
            marketImpactExplanation = "Jab 10Y Treasury Yield 4.5% ya 5% par chali jaye to log Bitcoin chor kar wahan bhaagte hain. Jab Yield girti hai to Bitcoin ooper jata hai.",
            whatTraderShouldWatch = "US 10-Year Bond Yield (US10Y). Iska crypto ke sath aam tor par ulta (inverse) taluq hota hai."
        ),
        FinancialConcept(
            id = "dxy",
            category = ConceptCategory.ECONOMIC_CONCEPTS,
            term = "DXY",
            fullForm = "US Dollar Index",
            simpleUrduMeaning = "Dunya ki 6 bari currencies (Euro, Yen, Pound waghera) ke muqable mein Americi Dollar ki taqat naapne ka paimana.",
            everydayExample = "Tole par aik taraf Dollar aur doosri taraf dunya ki baqi currencies.",
            whyItMatters = "Bitcoin aur Gold ki qeemat dollar mein tay hoti hai. Agar paimana khud mazboot ho jaye to cheez sasti lagti hai.",
            marketImpactExplanation = "DXY Ooper = Bitcoin Neeche (Bearish). DXY Neeche = Bitcoin Ooper (Bullish). Yeh trading ka ahem tareen qanoon hai.",
            whatTraderShouldWatch = "DXY ka 100, 104 aur 106 ka level."
        ),
        FinancialConcept(
            id = "monetary_policy",
            category = ConceptCategory.ECONOMIC_CONCEPTS,
            term = "Monetary Policy",
            fullForm = "Zar-i Policy (Paison Ki Gardish Ka Qanoon)",
            simpleUrduMeaning = "Central bank ka woh tareeqa-kar jisse woh tay karta hai ke market mein paisa zyada phenkna hai ya wapis kheench lena hai.",
            everydayExample = "Pani ki motor ka pipe. Valve khol diya to har taraf paani phail jayega, valve band kiya to sookha par jayega.",
            whyItMatters = "Yahi policy tay karti hai ke agle 2 saal karobar mein tezi aayegi ya khamoshi.",
            marketImpactExplanation = "Aasan policy mein tamam financial assets ki qeematain barhti hain.",
            whatTraderShouldWatch = "Fed, ECB aur Bank of Japan ke policy statements."
        ),
        FinancialConcept(
            id = "monetary_tightening",
            category = ConceptCategory.ECONOMIC_CONCEPTS,
            term = "Monetary Tightening",
            fullForm = "QT (Quantitative Tightening) — Sakht Policy",
            simpleUrduMeaning = "Central bank ka market se fazool dollar wapis nikaal lena aur sood barha dena taake mehngai qaboo ho.",
            everydayExample = "Walid sahab ka bacho ki pocket money kam kar dena taake fazool kharchi na ho.",
            whyItMatters = "Tightening ke doran liquidity sookh jati hai aur qarz lena mehanga ho jata hai.",
            marketImpactExplanation = "Crypto ke liye sakht dor (Bear Market). 2022 mein Fed ne tightening ki thi jis se BTC $69,000 se $15,500 tak gira tha.",
            whatTraderShouldWatch = "Fed Balance Sheet reduction ka size."
        ),
        FinancialConcept(
            id = "monetary_easing",
            category = ConceptCategory.ECONOMIC_CONCEPTS,
            term = "Monetary Easing",
            fullForm = "QE (Quantitative Easing) — Paison Ki Rail-Pail",
            simpleUrduMeaning = "Central bank ka naye note chhaap kar market mein phainkna aur sood 0% ke qareeb le aana.",
            everydayExample = "Achanak sab logon ko muft ya nihayat saste qarzay milna shuru ho jayen.",
            whyItMatters = "Jab har taraf paisa hi paisa hota hai to log har cheez khareedna shuru kar dete hain.",
            marketImpactExplanation = "Crypto ke liye sunheri dor (Parabolic Bull Market). 2020-2021 mein QE ki wajah se Bitcoin $4,000 se $69,000 tak gaya tha.",
            whatTraderShouldWatch = "Central Banks ka money printing ailan."
        ),
        FinancialConcept(
            id = "liquidity",
            category = ConceptCategory.ECONOMIC_CONCEPTS,
            term = "Liquidity",
            fullForm = "Rawaani (Market Ka Khoon)",
            simpleUrduMeaning = "Market mein asani se kisi cheez ko naqad (cash) mein badalne ki salahiyat, aur dunya mein ghoomne wale azaad dollar.",
            everydayExample = "Agar aapke paas sona hai to foran bik jayega (High Liquidity). Agar aapke paas sehra mein zameen hai to saalon nahi bikegi (Low Liquidity).",
            whyItMatters = "Liquidity ke baghair koi bhi market ooper nahi ja sakti. Bitcoin liquidity ka sab se bara spong hai.",
            marketImpactExplanation = "Jab Global Liquidity Index barhta hai to Bitcoin sab se tez daurta hai.",
            whatTraderShouldWatch = "Global M2 money supply aur central bank balance sheets."
        ),
        FinancialConcept(
            id = "money_supply",
            category = ConceptCategory.ECONOMIC_CONCEPTS,
            term = "Money Supply",
            fullForm = "M2 Money Supply (Kul Chhape Huwe Paison Ka Majmooa)",
            simpleUrduMeaning = "Dunya mein is waqt kitne naqad currency note, bank accounts aur checks mojood hain.",
            everydayExample = "Agar aik kamray mein 10 seb hon aur logon ke paas kul 100 rupay hon to seb 10 ka hoga. Agar logon ko 1000 rupay de diye jayen to wohi seb 100 ka ho jayega.",
            whyItMatters = "Jab bhi hukoomat naye note chhaapti hai, purane paison ki qadar gir jati hai aur Bitcoin ki demand barh jati hai kyunki Bitcoin sirf 21 million hi hain.",
            marketImpactExplanation = "M2 barhna Bitcoin ke liye sab se bara fuel hai.",
            whatTraderShouldWatch = "US aur Global M2 Growth Chart."
        ),
        FinancialConcept(
            id = "recession",
            category = ConceptCategory.ECONOMIC_CONCEPTS,
            term = "Recession",
            fullForm = "Maashi Mandi (Maeeshat Ka Sukar Jana)",
            simpleUrduMeaning = "Jab musalsal 6 mahine (do quarters) tak mulk ki GDP manfi ho jaye, companiyan logon ko nikaal dein aur dukanain band hone lagain.",
            everydayExample = "Poore shehar mein sannaata chha jaye, log kharch karna band kar dein aur dukanain khaali pari hon.",
            whyItMatters = "Recession ke aaghaz mein panic selling hoti hai.",
            marketImpactExplanation = "Aghaz mein risky assets girte hain, lekin baad mein hukoomat unhein bachane ke liye saste note chhaapti hai jo nayi bull market banati hai.",
            whatTraderShouldWatch = "Yield Curve Inversion aur Unemployment spikes."
        ),
        FinancialConcept(
            id = "economic_growth",
            category = ConceptCategory.ECONOMIC_CONCEPTS,
            term = "Economic Growth",
            fullForm = "Maashi Taraqqi",
            simpleUrduMeaning = "Mulk mein factories, dukanon aur services ka pehle se zyada maal peda karna aur logon ki aamdani barhna.",
            everydayExample = "Har saal naye ghar banna, nayi companiyan khulna aur logon ka maashee haal behtar hona.",
            whyItMatters = "Paeedaar taraqqi se tamam markets mein sarmayakari aati hai.",
            marketImpactExplanation = "Normal growth se markets mutawazan rehti hain.",
            whatTraderShouldWatch = "Corporate Earnings aur Purchasing Managers Index (PMI)."
        ),
        FinancialConcept(
            id = "purchasing_power",
            category = ConceptCategory.ECONOMIC_CONCEPTS,
            term = "Purchasing Power",
            fullForm = "Kharidari Ki Taqat",
            simpleUrduMeaning = "Aapke paas mojood aik note (jaise 1000 rupay ya $100) se is waqt kitna saaman khareeda ja sakta hai.",
            everydayExample = "20 saal pehle 100 rupay mein dawat ho jati thi, aaj 100 rupay mein aik cold drink bhi mushkil se milti hai. Iska matlab qadar 90% gir gayi.",
            whyItMatters = "Fiat currency (Dollar, Rupee) ki taqat waqt ke sath hamesha girti hai.",
            marketImpactExplanation = "Bitcoin ko 'Digital Gold' is liye kehte hain kyunki log apni purchasing power ko mehfooz karne ke liye isay khareedte hain.",
            whatTraderShouldWatch = "Real inflation adjusted returns."
        )
    )

    val cryptoConcepts: List<FinancialConcept> = listOf(
        FinancialConcept(
            id = "btc_dominance",
            category = ConceptCategory.CRYPTO_CONCEPTS,
            term = "Bitcoin Dominance",
            fullForm = "BTC.D (Crypto Market Par Bitcoin Ka Qabza)",
            simpleUrduMeaning = "Poori crypto dunya ke kul paison mein se akele Bitcoin ka kitna hissa (percentage) hai.",
            everydayExample = "Agar poori market mein 100 rupay hain aur 60 rupay akele Bitcoin mein hain, to Bitcoin Dominance 60% hai.",
            whyItMatters = "Yeh batata hai ke sarmayakar Altcoins mein paisa laga rahe hain ya sirf Bitcoin mein panah le rahe hain.",
            marketImpactExplanation = "BTC Dominance Girna + Total Market Cap Barhna = 'Altseason' (Altcoins 5x se 10x tezi se bhagte hain). BTC Dominance Barhna = Altcoins kamzor rehte hain.",
            whatTraderShouldWatch = "TradingView par BTC.D chart aur 55%-60% resistance zone."
        ),
        FinancialConcept(
            id = "market_cap",
            category = ConceptCategory.CRYPTO_CONCEPTS,
            term = "Market Capitalization",
            fullForm = "Market Cap (Kul Maliyat)",
            simpleUrduMeaning = "Kisi coin ki mojooda qeemat ko uske tamam coins ki tadaad se zarb (multiply) de kar nikaali gayi kul maliyat.",
            everydayExample = "Agar aik dukan mein 100 bakriyan hain aur aik bakri 50,000 ki hai, to kul dukan ki maliyat 50 lakh rupay hai.",
            whyItMatters = "Coin ka sasta ya mehnga hona sirf uski aik unit price se nahi balke Market Cap se pata chalta hai.",
            marketImpactExplanation = "Baray market cap wale coin (BTC, ETH) mehfooz hote hain lekin 10x aahista hote hain. Chhotay market cap wale coin bohat tezi se girte aur barhte hain.",
            whatTraderShouldWatch = "CoinMarketCap / CoinGecko par rank aur category."
        ),
        FinancialConcept(
            id = "trading_volume",
            category = ConceptCategory.CRYPTO_CONCEPTS,
            term = "Trading Volume",
            fullForm = "24 Ghante Ka Karobar",
            simpleUrduMeaning = "Pichle 24 ghanton mein kitne dollar ya coins ki khareed-o-farokht (trades) hui.",
            everydayExample = "Mandi mein kitni gariyon ka sauda hua. Agar mandi mein log bohat hon to sauda asani se ho jata hai.",
            whyItMatters = "Volume ke baghair aane wala breakout jhoota (fakeout) hota hai.",
            marketImpactExplanation = "High Volume ke sath agar price barhe to tezi pakki hoti hai. Low Volume par price barhna trap ho sakta hai.",
            whatTraderShouldWatch = "Chart ke neeche volume bars aur 20-period Volume MA."
        ),
        FinancialConcept(
            id = "circulating_supply",
            category = ConceptCategory.CRYPTO_CONCEPTS,
            term = "Circulating Supply",
            fullForm = "Gardish Mein Mojood Coins",
            simpleUrduMeaning = "Is waqt market mein aam logon aur exchanges ke paas kitne coins khareed-o-farokht ke liye dastiyab hain.",
            everydayExample = "Mandi mein is waqt kitni bori gandum bikne ke liye dukanon par rakhi hai.",
            whyItMatters = "Jo coin abhi market mein hain sirf wahi supply aur demand tay karte hain.",
            marketImpactExplanation = "Agar team ke paas bohat se coins band hon aur woh achanak unblock kar dein to price gir jati hai.",
            whatTraderShouldWatch = "Circulating Supply aur Total Supply ka farq."
        ),
        FinancialConcept(
            id = "total_supply",
            category = ConceptCategory.CRYPTO_CONCEPTS,
            term = "Total Supply",
            fullForm = "Kul Mojood Coins",
            simpleUrduMeaning = "Woh tamam coins jo is waqt ban chuke hain, chahe woh lock hon ya azaad ghoom rahe hon.",
            everydayExample = "Gandum jo dukan par hai aur woh jo godaam mein band pari hai, dono milakar total supply banti hai.",
            whyItMatters = "Pata chalta hai ke aane wale dino mein kitne mazeed coins market mein aa sakte hain.",
            marketImpactExplanation = "Bari supply wale coins ki price aam tor par fractional cents (jaise $0.001) hoti hai.",
            whatTraderShouldWatch = "Token unlock schedule."
        ),
        FinancialConcept(
            id = "max_supply",
            category = ConceptCategory.CRYPTO_CONCEPTS,
            term = "Maximum Supply",
            fullForm = "Hatamii Hadd (Aakhri Had)",
            simpleUrduMeaning = "Zindagi mein kabhi bhi is coin ke kul kitne daane ban sakte hain, is se aik bhi zyada nahi ban sakta.",
            everydayExample = "Jaise dunya mein sirf 21 piece nayab heere banaye gaye hon.",
            whyItMatters = "Bitcoin ki Max Supply sakhti se 21 Million (2 crore 10 lakh) fix hai. Koi hukoomat ya shakhs isay barha nahi sakta.",
            marketImpactExplanation = "Limited supply hamesha long-term mein qeemat ko barhati hai jab demand barhti hai.",
            whatTraderShouldWatch = "Kya coin ki max supply fix hai ya infinite (hamesha bante rahenge)."
        ),
        FinancialConcept(
            id = "btc_halving",
            category = ConceptCategory.CRYPTO_CONCEPTS,
            term = "Bitcoin Halving",
            fullForm = "Bitcoin Ki Aadhi Paidawar",
            simpleUrduMeaning = "Har 4 saal (210,000 blocks) baad Bitcoin miners ko milne wala inaam aadha (50% kam) ho jata hai.",
            everydayExample = "Sone ki kaan se pehle roz 10 tolay sona nikalta tha, ab achanak mehnat utni hi lagegi lekin sirf 5 tolay niklega.",
            whyItMatters = "Naye Bitcoin aane ki raftaar aadhi ho jati hai jabke demand barh rahi hoti hai.",
            marketImpactExplanation = "Tareekhi tor par har Halving ke agle 12 se 18 mahinon mein Bitcoin naya All-Time High banata hai.",
            whatTraderShouldWatch = "Next halving countdown aur miner revenue."
        ),
        FinancialConcept(
            id = "crypto_etfs",
            category = ConceptCategory.CRYPTO_CONCEPTS,
            term = "Crypto ETFs",
            fullForm = "Exchange Traded Funds (Spot Bitcoin & Ethereum ETFs)",
            simpleUrduMeaning = "Aik aisi sahoolat jisse America ke aam ameer log aur pension funds apne regular stock broker (jaise BlackRock) ke zariye Bitcoin khareed sakte hain baghair crypto wallet ke.",
            everydayExample = "Ghar mein sona rakhne ke bajaye bank ki raseed khareed lena jo 100% asli sonay ki paband ho.",
            whyItMatters = "Trillions of dollars ke dunya ke sab se bare sarmayakar ab asani se crypto khareed sakte hain.",
            marketImpactExplanation = "BlackRock, Fidelity ke ETF aane se Bitcoin institutional asset ban chuka hai.",
            whatTraderShouldWatch = "Spot Bitcoin aur Ethereum ETFs ke daily inflow/outflow numbers."
        ),
        FinancialConcept(
            id = "etf_inflows",
            category = ConceptCategory.CRYPTO_CONCEPTS,
            term = "ETF Inflows",
            fullForm = "Naya Sarmaya Aana",
            simpleUrduMeaning = "Bari companiyon aur sarmayakaron ka ETF khareed kar market mein naya dollar daakhil karna.",
            everydayExample = "Roz subah dukan mein naye hazaron grahak aakar maal khareedna shuru kar dein.",
            whyItMatters = "Wall Street se asli naqad dollar sidha Bitcoin market mein dakhil hota hai.",
            marketImpactExplanation = "Musalsal positive inflow = Strong Bullish signal.",
            whatTraderShouldWatch = "Daily net inflow data (e.g., +$500 Million in a day)."
        ),
        FinancialConcept(
            id = "etf_outflows",
            category = ConceptCategory.CRYPTO_CONCEPTS,
            term = "ETF Outflows",
            fullForm = "Sarmaya Wapis Nikaalna",
            simpleUrduMeaning = "Institutions ka apna munafa book karke ya darr kar ETF bech kar dollar wapis nikaal lena.",
            everydayExample = "Log bank se apni bachat nikaal kar ghar le jayen.",
            whyItMatters = "Bari farokht se Bitcoin spot market par fori dabao aata hai.",
            marketImpactExplanation = "Musalsal negative outflow = Short-term Bearish pressure.",
            whatTraderShouldWatch = "Grayscale (GBTC) aur doosre funds ke outflows."
        ),
        FinancialConcept(
            id = "exchange_reserves",
            category = ConceptCategory.CRYPTO_CONCEPTS,
            term = "Exchange Reserves",
            fullForm = "Binance/Coinbase par Paray Coins",
            simpleUrduMeaning = "Dunya ki tamam trading exchanges par is waqt kitne Bitcoin bikne ke liye tayar paray hain.",
            everydayExample = "Mandi ke stalls par kitni boriyan mojood hain.",
            whyItMatters = "Jab log Bitcoin cold storage (apne hardware wallet) mein le jate hain to exchange par kami ho jati hai.",
            marketImpactExplanation = "Reserves Girna = Log lambe arsay ke liye jama kar rahe hain (Bullish Supply Shock). Reserves Barhna = Log bechne ke liye laa rahe hain (Bearish).",
            whatTraderShouldWatch = "CryptoQuant aur Glassnode ke Exchange Netflow metrics."
        ),
        FinancialConcept(
            id = "stablecoins",
            category = ConceptCategory.CRYPTO_CONCEPTS,
            term = "Stablecoins",
            fullForm = "Mustahkam Digital Dollar (USDT, USDC)",
            simpleUrduMeaning = "Aise crypto coins jinki qeemat hamesha 1 US Dollar ke barabar fix rehti hai.",
            everydayExample = "Casino mein ghuste waqt naqad paison ke badlay milne wale chips.",
            whyItMatters = "Jab trader trade se nikalta hai to stablecoin mein baithta hai taake qeemat girne ka darr na ho.",
            marketImpactExplanation = "Stablecoins ki chhapayi barhna = Market mein khareedari ke liye 'Dry Powder' (tayyar sarmaya) barh gaya hai.",
            whatTraderShouldWatch = "Tether (USDT) aur Circle (USDC) ka total market cap."
        ),
        FinancialConcept(
            id = "whale_activity",
            category = ConceptCategory.CRYPTO_CONCEPTS,
            term = "Whale Activity",
            fullForm = "Baray Sarmayakaron Ki Harkat",
            simpleUrduMeaning = "Aise afraad ya idaray jinke paas hazaron Bitcoin hon (1,000+ BTC) unka achanak khareedna ya bechna.",
            everydayExample = "Talab mein bari machhli (whale) jidhar murray wahan paani mein bara toofan aata hai.",
            whyItMatters = "Aam retail traders ki nisbat Whales market ka rukh tay karti hain.",
            marketImpactExplanation = "Whales aam tor par chhotay traders ko dara kar unse saste mein maal chheen leti hain.",
            whatTraderShouldWatch = "Whale Alert alerts aur on-chain wallet movements."
        ),
        FinancialConcept(
            id = "institutional_investment",
            category = ConceptCategory.CRYPTO_CONCEPTS,
            term = "Institutional Investment",
            fullForm = "Idarayati Sarmayakari",
            simpleUrduMeaning = "Hedge funds, pension funds, public companies (MicroStrategy, Tesla) aur banks ka crypto khareedna.",
            everydayExample = "Galli ke chhotay dukan-dar ke bajaye aik multi-billionaire sarmayakar ka poora plaza khareed lena.",
            whyItMatters = "Yeh log $100 ya $500 nahi balke $100 Million se $1 Billion tak lagate hain.",
            marketImpactExplanation = "Institutional buying market ko mature aur legitimate banati hai.",
            whatTraderShouldWatch = "Michael Saylor (MicroStrategy) ki weekly Bitcoin purchases."
        ),
        FinancialConcept(
            id = "liquidation",
            category = ConceptCategory.CRYPTO_CONCEPTS,
            term = "Liquidation",
            fullForm = "Zabti (Account Khali Ho Jana)",
            simpleUrduMeaning = "Jab leverage par trade lene wale trader ka nuqsaan uske margin se zyada hone lage to exchange uski trade zabardasti band karke sara balance zabt kar leti hai.",
            everydayExample = "Aapne 10 rupay de kar 100 ka sauda kiya, agar qeemat 90 par aayi to dukandar ne aapke 10 rupay rakh liye aur sauda khatam kar diya.",
            whyItMatters = "Futures market mein hazaron trades aik sath liquidate hoti hain to market mein aag lag jati hai.",
            marketImpactExplanation = "Liquidation Cascade = Achanak 5 minute mein hazaron Longs liquidate hone se price $2,000 neeche gir sakti hai.",
            whatTraderShouldWatch = "Liquidation Heatmap aur Coinglass liquidation totals."
        ),
        FinancialConcept(
            id = "open_interest",
            category = ConceptCategory.CRYPTO_CONCEPTS,
            term = "Open Interest",
            fullForm = "OI (Khule Huwe Muahiday)",
            simpleUrduMeaning = "Futures market mein is waqt kul kitne contract khule (active) paray hain jo abhi tak band nahi kiye gaye.",
            everydayExample = "Maidan mein is waqt kitne dawaydaar kharay hain jinhone baazi lagayi hui hai.",
            whyItMatters = "Yeh batata hai ke market mein naya paisa aa raha hai ya purana paisa nikal raha hai.",
            marketImpactExplanation = "Price barhna + OI barhna = Mazboot tezi. Price barhna lekin OI girna = Kamzor tezi (Short covering).",
            whatTraderShouldWatch = "Futures Analytics mein Open Interest metric."
        ),
        FinancialConcept(
            id = "funding_rate",
            category = ConceptCategory.CRYPTO_CONCEPTS,
            term = "Funding Rate",
            fullForm = "Fees Ka Tabadla (Har 8 Ghantay Baad)",
            simpleUrduMeaning = "Futures price ko Spot price ke barabar rakhne ke liye Longs aur Shorts ke darmiyan aik doosre ko ada ki jane wali fees.",
            everydayExample = "Agar taxi mein bethne ke liye bohat lamba rush ho to taxi wala extra bhara maangta hai.",
            whyItMatters = "Positive funding ka matlab hai log had se zyada pur-umeed (bullish) hain aur Longs Shorts ko fees de rahe hain.",
            marketImpactExplanation = "Bohat zyada Positive Funding (+0.05% ya +0.1%) par market aam tor par neeche dump karti hai taake over-leveraged Longs ko saaf kiya ja sake.",
            whatTraderShouldWatch = "Binance Futures Funding Rate (0.01% standard normal hota hai)."
        ),
        FinancialConcept(
            id = "long_position",
            category = ConceptCategory.CRYPTO_CONCEPTS,
            term = "Long Position",
            fullForm = "Khareedari Ka Sauda (BUY)",
            simpleUrduMeaning = "Is umeed par trade kholna ke qeemat ooper jayegi taake ooper bech kar munafa kamaya ja sake.",
            everydayExample = "Mandi mein gandum 50 rupay koli aur tawaqqo ki ke agle haftay 60 rupay bikegi.",
            whyItMatters = "Aam trading ka bunyadi tareeqa.",
            marketImpactExplanation = "Jab bohot zyada log Long hote hain to market unka Stop Loss hunt karne neeche aati hai.",
            whatTraderShouldWatch = "Long/Short ratio."
        ),
        FinancialConcept(
            id = "short_position",
            category = ConceptCategory.CRYPTO_CONCEPTS,
            term = "Short Position",
            fullForm = "Farokht Ka Sauda (SELL)",
            simpleUrduMeaning = "Futures market mein qeemat girne par munafa kamana. Pehle mehenga bechna aur girne par sasta wapis khareedna.",
            everydayExample = "Dost se gari udhaar le kar 10 lakh ki bech di, jab gari ki qeemat 8 lakh hui to wapis khareed kar dost ko gari wapis kar di aur 2 lakh munafa jeb mein daal liya.",
            whyItMatters = "Iske zariye bear market mein bhi munafa banaya ja sakta hai.",
            marketImpactExplanation = "Short Squeeze: Jab achanak price ooper bhaagti hai aur tamam Shorts ko majbooran buy karna parta hai jis se price rocket ban jati hai.",
            whatTraderShouldWatch = "Short concentration levels."
        ),
        FinancialConcept(
            id = "leverage",
            category = ConceptCategory.CRYPTO_CONCEPTS,
            term = "Leverage",
            fullForm = "Udhaar Ka Gunnah (5x, 10x, 50x)",
            simpleUrduMeaning = "Exchange se udhaar le kar apni aukaat se bara sauda kholna.",
            everydayExample = "Aapke paas $100 hain, aapne 10x leverage liya to aap $1,000 ka Bitcoin khareed sakte hain.",
            whyItMatters = "Yeh do-dhari talwar hai. Munafa bhi 10 guna zyada hoga, lekin agar market 10% girti to aapka poora $100 foran zero (liquidate) ho jayega.",
            marketImpactExplanation = "Naye traders ke nuqsaan ki 95% wajah had se zyada leverage (20x, 50x) hota hai. Professional traders 2x se 5x se zyada nahi lete.",
            whatTraderShouldWatch = "Leverage ko hamesha 5x se kam rakhna aur Stop Loss lazmi lagana."
        ),
        FinancialConcept(
            id = "volatility",
            category = ConceptCategory.CRYPTO_CONCEPTS,
            term = "Volatility",
            fullForm = "Raftaar Aur Utaar-Charao",
            simpleUrduMeaning = "Qeemat kitni tezi aur kitne bare faaslay se ooper neeche hil rahi hai.",
            everydayExample = "Samundar ki pursukoon lehar ke muqable mein toofani maujain.",
            whyItMatters = "Crypto dunya ki sab se zyada volatile market hai. Yahan 10 minute mein 10% up ya down normal baat hai.",
            marketImpactExplanation = "High volatility mein beginner traders panic mein galat trades karte hain.",
            whatTraderShouldWatch = "ATR (Average True Range) indicator aur Bollinger Bandwidth."
        ),
        FinancialConcept(
            id = "market_sentiment",
            category = ConceptCategory.CRYPTO_CONCEPTS,
            term = "Market Sentiment",
            fullForm = "Jazbaat (Khauf aur Laalach)",
            simpleUrduMeaning = "Market mein mojood tamam traders ka majmooi jazbati mizaj — kya woh darr rahe hain ya laalach mein andhe ho rahe hain.",
            everydayExample = "Bazaari afwah. Aik din sab khareedne ke liye pagal hon aur agle din har shakhs darr kar bhaag raha ho.",
            whyItMatters = "Warren Buffett ka qanoon: 'Jab dunya laalchi ho to daro, aur jab dunya darr rahi ho to laalchi bano.'",
            marketImpactExplanation = "Extreme Fear = Aksar khareedari ka behtareen waqt. Extreme Greed = Top banne aur bechne ka waqt.",
            whatTraderShouldWatch = "Crypto Fear & Greed Index (0 se 100)."
        ),
        FinancialConcept(
            id = "support",
            category = ConceptCategory.CRYPTO_CONCEPTS,
            term = "Support",
            fullForm = "Sahara (Farsh)",
            simpleUrduMeaning = "Chart par woh qeemat jahan aakar girawat rukti hai kyunki wahan bohat se kharidaar tayar baithay hote hain.",
            everydayExample = "Ghar ka pakka farsh jis par gir kar gaind wapis uchhalti hai.",
            whyItMatters = "Support par khareedna aur uske thora neeche Stop Loss lagana aqlmandi hai.",
            marketImpactExplanation = "Agar mazboot support toot jaye to qeemat achanak aglay floor tak tezi se girti hai.",
            whatTraderShouldWatch = "Pichle swing lows aur order blocks."
        ),
        FinancialConcept(
            id = "resistance",
            category = ConceptCategory.CRYPTO_CONCEPTS,
            term = "Resistance",
            fullForm = "Rukawat (Chhat)",
            simpleUrduMeaning = "Chart par woh qeemat jahan se ooper jana mushkil hota hai kyunki wahan bechne wale sellers ka hujoom hota hai.",
            everydayExample = "Kamray ki chhat jis se gaind takra kar wapis neeche aati hai.",
            whyItMatters = "Resistance par khareedari nahi karni chahiye balke munafa lena chahiye.",
            marketImpactExplanation = "Jab resistance volume ke sath toot jaye to wohi chhat aage chal kar naya farsh (Support) ban jati hai.",
            whatTraderShouldWatch = "Pichle swing highs aur supply zones."
        ),
        FinancialConcept(
            id = "correlation",
            category = ConceptCategory.CRYPTO_CONCEPTS,
            term = "Correlation",
            fullForm = "Bahami Taluq",
            simpleUrduMeaning = "Do mukhtalif cheezon ka aik sath aik hi rukh par chalna ya mukhalif chalna.",
            everydayExample = "Barsaat aur chhatri ki bikri ka taluq.",
            whyItMatters = "Bitcoin aksar America ke Nasdaq tech stocks aur Gold ke sath taluq dikhata hai.",
            marketImpactExplanation = "Positive correlation mein agar Nasdaq barhe to Bitcoin bhi barhta hai. Negative correlation mein agar Dollar (DXY) barhe to Bitcoin girta hai.",
            whatTraderShouldWatch = "BTC vs S&P500 aur BTC vs Gold correlation coefficient."
        ),
        FinancialConcept(
            id = "risk_on",
            category = ConceptCategory.CRYPTO_CONCEPTS,
            term = "Risk-On",
            fullForm = "Khatra Mol Lene Ka Mizaj",
            simpleUrduMeaning = "Jab maeeshat achhi ho aur sarmayakar munafa kamane ke liye zyada khatray wale assets (Crypto, Tech Stocks) mein diler ho kar paisa lagate hain.",
            everydayExample = "Bahaar ke mosam mein kisan ka khul kar nayi fasal lagana.",
            whyItMatters = "Risk-on mahol crypto bull runs ke liye shart-e-awwal hai.",
            marketImpactExplanation = "Risk-On doran Bitcoin aur Altcoins sab se zyada munafa dete hain.",
            whatTraderShouldWatch = "Stock market rallies aur credit spreads."
        ),
        FinancialConcept(
            id = "risk_off",
            category = ConceptCategory.CRYPTO_CONCEPTS,
            term = "Risk-Off",
            fullForm = "Khatray Se Bachne Ka Mizaj (Darr)",
            simpleUrduMeaning = "Jab jang, bohran ya recession ka darr ho to log risky cheezon se paisa nikaal kar Dollar, Sona (Gold) aur Cash mein mehfooz ho jate hain.",
            everydayExample = "Toofan ki khabar sun kar sab logon ka apne gharon mein ghus jana aur dukanain band kar dena.",
            whyItMatters = "Is doran crypto mein shaded bikwali aati hai.",
            marketImpactExplanation = "Risk-Off din par Bitcoin $2,000-$5,000 gir sakta hai jabke Dollar Index (DXY) aur Gold barhte hain.",
            whatTraderShouldWatch = "VIX (Volatility Index) aur geopolitical tension news."
        ),
        FinancialConcept(
            id = "safe_haven",
            category = ConceptCategory.CRYPTO_CONCEPTS,
            term = "Safe Haven",
            fullForm = "Aman Ki Panahgaah",
            simpleUrduMeaning = "Aisi sarmayakari jismein toofan aur jang ke waqt bhi paisa doobne ka khatra sab se kam ho (jaise Sona ya US Treasury Bonds).",
            everydayExample = "Toofani darya ke beech pukhta qila.",
            whyItMatters = "Sarmayakar jang aur bohran ke doran safe haven assets dhoondte hain.",
            marketImpactExplanation = "Gold hamesha safe haven raha hai. Bitcoin ab aahista aahista 'Digital Gold' aur Safe Haven banne ki taraf safar kar raha hai.",
            whatTraderShouldWatch = "Gold prices aur global crisis reactions."
        )
    )
}
