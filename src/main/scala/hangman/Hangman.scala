package hangman

import java.io.IOException

import scalaz.zio.console._
import scalaz.zio._

import scala.language.postfixOps

object Hangman extends App {

  case class State(name: String, guesses: Set[Char] = Set.empty[Char], word: String) {
    final def failures: Int = (guesses -- word.toSet).size
    final def playerLost: Boolean = failures >= 10
    final def playerWon: Boolean = (word.toSet -- guesses).isEmpty
  }

  lazy val Dictionary: List[String] = scala.io.Source.fromResource("words.txt").getLines.toList

  val getName: ZIO[Console, IOException, String] = putStrLn("What is your name: ") *> getStrLn
  val getLine: ZIO[Console, IOException, String] = putStrLn(s"Please enter a letter") *> getStrLn

  val getChoice : ZIO[Console, IOException, Char] = for {
    line <- getLine
    char <- line.toLowerCase.trim.headOption match {
      case Some(x) => IO.succeed(x)
      case None    => putStrLn(s"You did not enter a character") *> getChoice
    }
  } yield char

  val chooseWord: IO[IOException, String] = for {
    rand <- nextInt(Dictionary.length)
  } yield Dictionary lift rand getOrElse "Bug in the program!"

  val hangman : ZIO[Console, IOException, Unit] = for {
    _ <- putStrLn("Welcome to purely functional hangman")
    name <- getName
    _ <- putStrLn(s"Welcome $name. Let's begin!")
    word <- chooseWord
    state = State(name, Set(), word)
    _ <- renderState(state)
    _ <- gameLoop(state)
  } yield()

  def gameLoop(state: State) : ZIO[Console, IOException, State] = {
    for {
      guess <- getChoice
      state <- IO.succeed(state.copy(guesses = state.guesses + guess))
      _ <- renderState(state)
      loop <- if (state.playerWon) putStrLn(s"Congratulations ${state.name} you won the game!").const(false)
      else if (state.playerLost) putStrLn(s"Sorry ${state.name} you lost the game. The word was ${state.word}").map(_ => false).const(false)
      else if (state.word.contains(guess)) putStrLn(s"You guessed correctly!").const(true)
      else putStrLn(s"That's wrong. but keep trying!").const(true)
      state <- if (loop) gameLoop(state) else IO.succeed(state)
    } yield state
  }


  def nextInt(max: Int) : IO[Nothing, Int] = ??? //IO.sync(scala.util.Random.nextInt(max))

  def renderState(state: State) : ZIO[Console, Nothing, Unit] = {
    val word = state.word.toList.map(c =>
      if (state.guesses.contains(c)) s" $c " else " "
    ) mkString
    val line = List.fill(state.word.length)(" - ").mkString
    val guesses = " Guesses: " + state.guesses.toList.sorted.mkString
    val text = word + "\n" + line + "\n\n" + guesses + "\n"
    putStrLn(text)
  }

  override def run(args: List[String]): ZIO[Environment, Nothing, Int] = {
    UIO(1)
    //hangman.redeem(
    //  _ => IO.succeed(ExitStatus.ExitNow(1)),
    //  _ => IO.succeed(ExitStatus.ExitNow(0))
    //)
  }
}
