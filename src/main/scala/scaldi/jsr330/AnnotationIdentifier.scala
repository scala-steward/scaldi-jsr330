package scaldi.jsr330

import java.lang.annotation.Annotation
import javax.inject.Qualifier

import scaldi.util.ReflectionHelper
import scaldi.{Identifier, InjectException}

import scala.reflect.runtime.universe.{Type, TypeTag}

case class AnnotationIdentifier(tpe: Type, annotation: Option[Annotation] = None) extends Identifier {
  def sameAs(other: Identifier): Boolean =
    other match {
      case AnnotationIdentifier(otherTpe, otherAnnotation) if ReflectionHelper.isAssignableFrom(otherTpe, tpe) =>
        (annotation, otherAnnotation) match {
          case (None, _)           => true
          case (Some(a), Some(oa)) => a == oa
          case _                   => false
        }
      case _ => false
    }

  override def required = true
}

object AnnotationIdentifier {
  def qualifier[T <: Annotation: TypeTag]: AnnotationIdentifier = {
    val tpe = implicitly[TypeTag[T]].tpe

    if (!ReflectionHelper.hasAnnotation[Qualifier](tpe))
      throw new InjectException(
        s"Annotation `$tpe` is not a qualifier annotation."
      )

    AnnotationIdentifier(tpe)
  }

  def annotation[A <: Annotation: TypeTag](a: A): AnnotationIdentifier = {
    if (!ReflectionHelper.hasAnnotation[Qualifier](a))
      throw new InjectException(
        s"Annotation `$a` is not a qualifier annotation."
      )

    AnnotationIdentifier(implicitly[TypeTag[A]].tpe, Some(a))
  }

  def forAnnotation(a: Annotation): AnnotationIdentifier = {
    if (!ReflectionHelper.hasAnnotation[Qualifier](a))
      throw new InjectException(
        s"Annotation `$a` is not a qualifier annotation."
      )

    AnnotationIdentifier(
      ReflectionHelper.classToType(a.annotationType()),
      Some(a)
    )
  }
}
