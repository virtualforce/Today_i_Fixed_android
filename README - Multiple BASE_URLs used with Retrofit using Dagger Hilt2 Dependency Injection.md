## Problem
   How to use Multiple BASE_URLs with Retrofit using Dagger Hilt2 Dependency Injection.

## How you fix it
You can achieve by using Qualifier. Let's see an example:

## Solution
1. Create a kotlin class e.g. Qualifiers.kt and defined Qualifier you need

```
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Auth

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Setting

```

2. Create module class e.g. NetworkModule.kt
```
@Provides
@Singleton
@Auth   //This will differentiate retrofit object
fun retrofitAuth(
   client: OkHttpClient,
   gsonConverterFactory: GsonConverterFactory
): Retrofit =
     Retrofit.Builder()
         .client(client)
         .addConverterFactory(gsonConverterFactory)
         .baseUrl("https://someauth.baseurl.com").build()


@Provides
@Singleton
@Setting   //This will differentiate retrofit object
fun retrofitSetting(
   client: OkHttpClient,
   gsonConverterFactory: GsonConverterFactory
): Retrofit =
     Retrofit.Builder()
         .client(client)
         .addConverterFactory(gsonConverterFactory)
         .baseUrl("https://someSetting.baseurl.com").build()


 //Build Api services with respect to qualifiers

 @Provides
 @Singleton
 fun authApiService(@Auth retrofit: Retrofit): AuthApiService = retrofit.create(AuthApiService::class.java)


 @Provides
 @Singleton
 fun settingApiService(@Setting retrofit: Retrofit): SettingApiService = retrofit.create(SettingApiService::class.java)

```

3. Create separate Api Service interfaces e.g. AuthApiService and SettingApiService
```
interface AuthApiService {

      @FormUrlEncoded
      @POST("login/v2")
      fun login(@FieldMap params: HashMap<String, Any>): Response<LoginResponse>
 }


 interface SettingApiService {

      @GET("settings")
      fun settings(): Response<SettingsResponse>

      @GET("faqs")
      fun getFAQs(): Response<FAQsResponse>

 }

```
