package org.netmelody.cii.persistence;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.netmelody.cii.domain.Sponsor;

public final class Detective {
    
    private static final Pattern PICTURE_FILENAME_REGEX = Pattern.compile("^\\s*\\[(.*)\\]\\s*$");
    
    public Map<String, Sponsor> userMap = new HashMap<String, Sponsor>();
    
    public Detective(File picturesFile) {
        loadPictureSettings(picturesFile);
    }

    private void loadPictureSettings(File picturesFile) {
        userMap.clear();
        if (!picturesFile.canRead()) {
            return;
        }
        
        try {
            final List<String> content = FileUtils.readLines(picturesFile);
            extractPicuresFrom(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void extractPicuresFrom(List<String> content) {
        String pictureFilename = "";
        final List<String> aliases = new ArrayList<String>();
        
        for (String line : content) {
            Matcher pictureFilenameMatcher = PICTURE_FILENAME_REGEX.matcher(line);
            if (pictureFilenameMatcher.matches()) {
                if (!pictureFilename.isEmpty() && !aliases.isEmpty()) {
                    registerUser(aliases.get(0), "/pictures/" + pictureFilename, aliases.toArray(new String[aliases.size()]));
                }
                pictureFilename = pictureFilenameMatcher.group(1);
                aliases.clear();
                continue;
            }
            
            if (!line.isEmpty()) {
                aliases.add(line);
            }
        }
        
        if (!pictureFilename.isEmpty() && !aliases.isEmpty()) {
            registerUser(aliases.get(0), "/pictures/" + pictureFilename, aliases.toArray(new String[aliases.size()]));
        }
    }
    
    private void registerUser(String name, String pictureUrl, String... keywords) {
        final Sponsor user = new Sponsor(name, pictureUrl);
        userMap.put(name.toUpperCase(), user);
        for (String keyword : keywords) {
            userMap.put(keyword.toUpperCase(), user);
        }
    }
    
    public List<Sponsor> sponsorsOf(String changeText) {
        final Collection<Sponsor> sponsors = new HashSet<Sponsor>();
        
        final String upperChangeText = changeText.toUpperCase();
        for (String keyword : userMap.keySet()) {
            if (upperChangeText.contains(keyword)) {
                sponsors.add(userMap.get(keyword));
            }
        }
        return new ArrayList<Sponsor>(sponsors);
    }

}
