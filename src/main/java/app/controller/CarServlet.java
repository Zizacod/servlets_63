package app.controller;

import app.model.Car;
import app.repository.CarRepository;
import app.repository.CarRepositoryDB;
import app.repository.CarRepositoryHibernate;
import app.repository.CarRepositoryMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class CarServlet extends HttpServlet {

    private CarRepository repository = new CarRepositoryHibernate();

    private ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, String[]> params = request.getParameterMap();

        if (params.isEmpty()){

            List<Car> cars = repository.getAll();

            String jsonResponse = mapper.writeValueAsString(cars);
            response.getWriter().write(jsonResponse);

        } else {
            String idString = params.get("id")[0];
            long id = Long.parseLong(idString);


            String idString2 = request.getParameter("id");




            Car car = repository.getById(id);

            if (car == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"message\" : \"Car not found\"}");
            } else {
                String json = mapper.writeValueAsString(car);
                response.getWriter().write(json);
            }

        }




//        cars.forEach(car -> {
//            try {
//                response.getWriter().write(car.toString() + "\n");
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
 //       });



    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Car car = mapper.readValue(request.getReader(), Car.class);

        car = repository.save(car);

        response.getWriter().write(mapper.writeValueAsString(car));
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        
        try {
            // Читаем данные автомобиля из тела запроса
            Car carToUpdate = mapper.readValue(req.getReader(), Car.class);
            
            // Проверяем, что ID указан
            if (carToUpdate.getId() == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                // отправляем ошибку в теле нашего ответа
                resp.getWriter().write("{\"error\": \"ID is required for update\"}");
                return;
            }
            
            // Получаем существующий автомобиль
            Car existingCar = repository.getById(carToUpdate.getId());
            if (existingCar == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\": \"Car with ID " + carToUpdate.getId() + "\" not found\"}");
                return;
            }
            
            // Обновляем только переданные поля 
            if (carToUpdate.getBrand() != null) {
                existingCar = new Car(carToUpdate.getBrand(), existingCar.getPrice(), existingCar.getYear());
                existingCar.setId(carToUpdate.getId());
            }
            if (carToUpdate.getPrice() != null) {
                existingCar = new Car(existingCar.getBrand(), carToUpdate.getPrice(), existingCar.getYear());
                existingCar.setId(carToUpdate.getId());
            }
            if (carToUpdate.getYear() != 0) { // 0 - значение по умолчанию для int
                existingCar = new Car(existingCar.getBrand(), existingCar.getPrice(), carToUpdate.getYear());
                existingCar.setId(carToUpdate.getId());
            }
            
            // Обновляем автомобиль в репозитории
            Car updatedCar = repository.update(existingCar);
            
            // Возвращаем обновленный автомобиль
            String jsonResponse = mapper.writeValueAsString(updatedCar);
            resp.getWriter().write(jsonResponse);
            
        } catch (IllegalArgumentException e) {
            // Ошибки валидации от репозитория
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            // Ошибка если клиент отправил некорректные данные
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"Invalid request data: " + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        
        try {
            // Получаем ID из параметров запроса
            String idParam = req.getParameter("id");
            if (idParam == null || idParam.trim().isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\": \"ID parameter is required\"}");
                return;
            }
            
            // Парсим в long потому что:
            //HTTP-параметры всегда приходят как String
            //методы репозитория ожидают long
            long id = Long.parseLong(idParam);
            
            // Проверяем, существует ли автомобиль
            Car carToDelete = repository.getById(id);
            if (carToDelete == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\": \"Car with ID " + id + " not found\"}");
                return;
            }
            
            // Удаляем автомобиль
            boolean deleted = repository.delete(id);
            
            // если репозиторий вернул true
            if (deleted) {
                resp.getWriter().write("{\"message\": \"Car with ID " + id + " deleted successfully\"}");
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write("{\"error\": \"Failed to delete car\"}");
            }
            // попадем на это исключение при попытке пропарсить в 137 строке не число
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"Invalid ID format\"}");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"Server error: " + e.getMessage() + "\"}");
        }
    }

}
