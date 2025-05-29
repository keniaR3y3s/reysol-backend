package com.revoktek.reysol.core.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revoktek.reysol.core.enums.TipoClienteEnum;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

@Component
public class ApplicationUtil {

    private final ObjectMapper objectMapper;

    public ApplicationUtil(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public boolean isNull(Object object) {
        return object == null;
    }

    public boolean nonNull(Object object) {
        return !isNull(object);
    }

    public boolean nonEmpty(String value) {
        return nonNull(value) && !value.isEmpty() && !"null".equals(value);
    }

    public boolean isEmpty(String value) {
        return !nonEmpty(value);
    }


    public boolean nonEmptyList(List<?> list) {
        return nonNull(list) && !list.isEmpty();
    }

    public boolean isAlphabetic(String cadena) {
        Pattern pat = Pattern.compile("[A-Za-z]");
        return pat.matcher(cadena).find();
    }

    public boolean isAlphaNumeric(String cadena) {
        Pattern pat = Pattern.compile("[A-Za-z0-9]");
        return pat.matcher(cadena).find();
    }

    public boolean isEmailValid(String email) {
        Pattern pat = Pattern
                .compile("^[\\w-]+(\\.[\\w-]+)*@[A-Za-z0-9\u002D]+(\\.[A-Za-z0-9\u002D]+)*(\\.[A-Za-z]{2,})$");
        return pat.matcher(email).find();
    }

    public boolean isNumber(String numero) {
        final Pattern pat = Pattern.compile("^\\d+|\\d+(\\.\\d{1,2})?$");
        return pat.matcher(numero).find();
    }


    public boolean isEmptyList(List<?> list) {
        return !nonEmptyList(list);
    }

    public LocalDateTime now() {
        return LocalDateTime.now();
    }


    public Method findSetterMethod(Object entityClass, String fieldName) {
        String setterMethodName = "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
        String isMethodName = "is" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
        for (Method method : entityClass.getClass().getMethods()) {
            if ((method.getName().equals(setterMethodName) || method.getName().equals(isMethodName)) && method.getParameterCount() == 1) {
                return method;
            }
        }
        return null;

    }

    public void setDefaultValue(Object entityClass, String fieldName, Object... args) throws InvocationTargetException, IllegalAccessException {
        Method setEnabledMethod = findSetterMethod(entityClass, fieldName);
        if (setEnabledMethod != null) {
            setEnabledMethod.invoke(entityClass, args);
        }
    }

    public String getError(Exception e) {
        return e.getStackTrace()[0].getMethodName();
    }

    public String getPrefixPedido(Integer tipo) {
        if (Objects.equals(tipo, TipoClienteEnum.REGULAR.getValue())) {
            return "PD-";
        } else if (Objects.equals(tipo, TipoClienteEnum.EXTEMPORANEO.getValue())) {
            return "PDX-";
        }
        return "";

    }

    public String formatDate(Date date, int type) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        return format.format(date);
    }

    public String formatMoney(BigDecimal bd) {
        NumberFormat formatoMoneda = NumberFormat.getCurrencyInstance(new Locale("es", "MX")); // Formato en pesos mexicanos
        BigDecimal bdMoneda = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
       return formatoMoneda.format(bdMoneda);
    }
    public String formatBigDecimal(BigDecimal bd) {
        return bd.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    public String toJson(Object object) {
        try {
            if(isNull(object)) {
                return objectMapper.writeValueAsString(new HashMap<String, Object>() {
                });
            }
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error al convertir objeto a JSON", e);
        }
    }
}
