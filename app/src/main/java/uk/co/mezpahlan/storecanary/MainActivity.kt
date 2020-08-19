package uk.co.mezpahlan.storecanary

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dropbox.android.external.store4.FetcherResult
import com.dropbox.android.external.store4.SourceOfTruth
import com.dropbox.android.external.store4.StoreBuilder
import com.dropbox.store.rx2.fromMaybe
import com.dropbox.store.rx2.singleFetcher
import com.dropbox.store.rx2.withScheduler
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import uk.co.mezpahlan.storecanary.databinding.ActivityMainBinding
import java.util.concurrent.atomic.AtomicInteger

class MainActivity : AppCompatActivity() {

    private val atomicInteger = AtomicInteger(0)
    private var fakeDisk = mutableMapOf<Int, String>()
    private val store = StoreBuilder.from(
        fetcher = singleFetcher<Int, String> { Single.fromCallable { FetcherResult.Data("$it ${atomicInteger.incrementAndGet()}") } },
        sourceOfTruth = SourceOfTruth.fromMaybe(
            reader = { Maybe.fromCallable<String> { fakeDisk[it] } },
            writer = { key, value -> Completable.fromAction { fakeDisk[key] = value } },
            delete = { Completable.fromAction { fakeDisk.remove(it) } },
            deleteAll = { Completable.fromAction { fakeDisk.clear() } }
        )
    )
        .withScheduler(Schedulers.io())
        .build()

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener {
            startActivity(Intent(this, SecondaryActivity::class.java))
        }


    }
}