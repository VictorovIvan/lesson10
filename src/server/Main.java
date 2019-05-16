package server;

/*
 * Задание 1. Разработать приложение - многопользовательский чат, в котором участвует произвольное количество клиентов.
 * Каждый клиент после запуска отправляет свое имя серверу. После чего начинает отправлять ему сообщения.
 * Каждое сообщение сервер подписывает именем клиента и рассылает всем клиентам (broadcast).
 * Задание 2.  Усовершенствовать задание 1:
 * a. Добавить возможность отправки личных сообщений (unicast).
 * b. Добавить возможность выхода из чата с помощью написанной в чате команды «quit»
 */

/*
 * Для выполнения пункта задание 2.а: Для отправки персонального сообщения требуется ввести в следущем формате-
 * (Текст сообщения)-To##client-(Имя клиента)
 * Пример в окне Client №1 вводим: Привет.-To##client-Client №3
 */
public class Main {

    public static void main(String[] args) {
        Server server = new Server();
    }
}