package scaldi

import java.lang.annotation.Annotation
import scala.reflect.runtime.universe.{typeTag, TypeTag}

package object jsr330 {
  def qualifier[T <: Annotation: TypeTag]: AnnotationIdentifier =
    AnnotationIdentifier.qualifier[T]

  def annotation[A <: Annotation: TypeTag](a: A): AnnotationIdentifier =
    AnnotationIdentifier.annotation[A](a)

  def annotated[T: TypeTag](implicit injector: () => Injector): WordBindingProvider[T] =
    WordBindingProvider[T](AnnotationBinding(Right(typeTag[T].tpe), injector, _, _, _))

  def annotated[T <: AnyRef: TypeTag](instance: T)(implicit
      injector: () => Injector
  ): WordBindingProvider[T] =
    WordBindingProvider[T](AnnotationBinding(Left(instance), injector, _, _, _))
}
