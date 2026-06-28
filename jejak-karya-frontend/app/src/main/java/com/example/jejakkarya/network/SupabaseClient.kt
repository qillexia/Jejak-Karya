package com.example.jejakkarya.network

import com.example.jejakkarya.network.Secrets
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseClient {
    val client = createSupabaseClient(
        supabaseUrl = Secrets.SUPABASE_URL,
        supabaseKey = Secrets.SUPABASE_KEY
    ) {
        install(Auth)
        install(Postgrest)
    }
}
