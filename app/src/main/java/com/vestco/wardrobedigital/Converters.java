package com.vestco.wardrobedigital;

import androidx.room.TypeConverter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Converters {

    @TypeConverter
    public String fromList(List<String> list) {
        return list != null ? String.join(",", list) : "";
    }

    @TypeConverter
    public List<String> toList(String value) {
        if (value == null || value.isEmpty()) return Collections.emptyList();
        return Arrays.stream(value.split(",")).map(String::trim).collect(Collectors.toList());
    }
}
