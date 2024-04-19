import com.github.dockerjava.api.async.ResultCallback
import com.github.dockerjava.core.DefaultDockerClientConfig
import com.github.dockerjava.core.DockerClientImpl
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient
import com.github.dockerjava.transport.DockerHttpClient
import java.io.PipedInputStream
import java.io.PipedOutputStream
import java.time.Duration

fun main(args: Array<String>) {

    val config = DefaultDockerClientConfig.createDefaultConfigBuilder()
        .withDockerHost("unix:///var/run/docker.sock")
//        .withDockerTlsVerify(true)
//        .withDockerCertPath("/Users/Ali/.docker")
        .build()

    val httpClient: DockerHttpClient = ApacheDockerHttpClient.Builder()
        .dockerHost(config.dockerHost)
        .sslConfig(config.sslConfig)
        .maxConnections(100)
        .connectionTimeout(Duration.ofSeconds(30))
        .responseTimeout(Duration.ofSeconds(45))
        .build()

    val dockerClient = DockerClientImpl.getInstance(config, httpClient)

    val callback = AttachContainerTestCallback()

    PipedOutputStream().use { out ->
        PipedInputStream(out).use { `in` ->
            dockerClient.attachContainerCmd("bruh")
                .withStdErr(true)
                .withStdOut(true)
                .withFollowStream(true)
                .withLogs(false)
                .withStdIn(`in`)
                .exec<ResultCallback<com.github.dockerjava.api.model.Frame>>(callback)

            while (true) {
                val s = readln()

                out.write((s + "\n").toByteArray())
                out.flush()

                Thread.sleep(100)
                println(callback.toString())
            }

        }
    }
}


class AttachContainerTestCallback : ResultCallback.Adapter<com.github.dockerjava.api.model.Frame>() {
    private var log = StringBuffer()
    override fun onNext(item: com.github.dockerjava.api.model.Frame) {
        log = StringBuffer()
        log.append(String(item.payload))
        super.onNext(item)
    }

    override fun toString(): String {
        return log.toString()
    }
}
