package io.isomarcte.shash.cats

import cats.effect._
import cats.implicits._
import javax.crypto._
import javax.crypto.spec.SecretKeySpec
import scodec.bits._

object HMAC {
  private[this] val hmacMd5Algorithm: String = "HmacMd5"

  def hmacMd5[F[_]](key: ByteVector)(value: ByteVector)(implicit F: Sync[F]): F[ByteVector] =
    for {
      key <- F.delay(new SecretKeySpec(key.toArray, this.hmacMd5Algorithm))
      mac <- F.delay(Mac.getInstance(this.hmacMd5Algorithm))
      _ <- F.delay(mac.init(key))
      result <- F.delay(ByteVector.view(mac.doFinal(value.toArray)))
    } yield result
}
