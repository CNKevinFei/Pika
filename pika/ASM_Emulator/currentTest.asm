        Jump         $$main                    
        DLabel       $eat-location-zero        
        DataZ        8                         
        DLabel       $print-format-integer     
        DataC        37                        %% "%d"
        DataC        100                       
        DataC        0                         
        DLabel       $print-format-float       
        DataC        37                        %% "%g"
        DataC        103                       
        DataC        0                         
        DLabel       $print-format-boolean     
        DataC        37                        %% "%s"
        DataC        115                       
        DataC        0                         
        DLabel       $print-format-string      
        DataC        37                        %% "%s"
        DataC        115                       
        DataC        0                         
        DLabel       $print-format-char        
        DataC        37                        %% "%c"
        DataC        99                        
        DataC        0                         
        DLabel       $print-format-newline     
        DataC        10                        %% "\n"
        DataC        0                         
        DLabel       $print-format-tab         
        DataC        9                         %% "\t"
        DataC        0                         
        DLabel       $print-format-space       
        DataC        32                        %% " "
        DataC        0                         
        DLabel       $boolean-true-string      
        DataC        116                       %% "true"
        DataC        114                       
        DataC        117                       
        DataC        101                       
        DataC        0                         
        DLabel       $boolean-false-string     
        DataC        102                       %% "false"
        DataC        97                        
        DataC        108                       
        DataC        115                       
        DataC        101                       
        DataC        0                         
        DLabel       $errors-general-message   
        DataC        82                        %% "Runtime error: %s\n"
        DataC        117                       
        DataC        110                       
        DataC        116                       
        DataC        105                       
        DataC        109                       
        DataC        101                       
        DataC        32                        
        DataC        101                       
        DataC        114                       
        DataC        114                       
        DataC        111                       
        DataC        114                       
        DataC        58                        
        DataC        32                        
        DataC        37                        
        DataC        115                       
        DataC        10                        
        DataC        0                         
        Label        $$general-runtime-error   
        PushD        $errors-general-message   
        Printf                                 
        Halt                                   
        DLabel       $errors-int-divide-by-zero 
        DataC        105                       %% "integer divide by zero"
        DataC        110                       
        DataC        116                       
        DataC        101                       
        DataC        103                       
        DataC        101                       
        DataC        114                       
        DataC        32                        
        DataC        100                       
        DataC        105                       
        DataC        118                       
        DataC        105                       
        DataC        100                       
        DataC        101                       
        DataC        32                        
        DataC        98                        
        DataC        121                       
        DataC        32                        
        DataC        122                       
        DataC        101                       
        DataC        114                       
        DataC        111                       
        DataC        0                         
        Label        $$i-divide-by-zero        
        PushD        $errors-int-divide-by-zero 
        Jump         $$general-runtime-error   
        DLabel       $usable-memory-start      
        DLabel       $global-memory-block      
        DataZ        50                        
        DLabel       $string-constant-memory   
        Label        $$main                    
        PushD        $global-memory-block      
        PushI        0                         
        Add                                    %% quarters
        PushF        1.100000                  
        ConvertI                               
        PushI        1                         
        PushI        6                         
        Add                                    
        Add                                    
        StoreI                                 
        PushD        $global-memory-block      
        PushI        4                         
        Add                                    %% dimes
        PushI        3                         
        StoreI                                 
        PushD        $global-memory-block      
        PushI        8                         
        Add                                    %% nickels
        PushI        7                         
        StoreI                                 
        PushD        $global-memory-block      
        PushI        12                        
        Add                                    %% pennies
        PushI        17                        
        StoreI                                 
        PushD        $global-memory-block      
        PushI        16                        
        Add                                    %% str
        DataC        104                       %% "hello kitty*const print \n"
        DataC        101                       
        DataC        108                       
        DataC        108                       
        DataC        111                       
        DataC        32                        
        DataC        107                       
        DataC        105                       
        DataC        116                       
        DataC        116                       
        DataC        121                       
        DataC        42                        
        DataC        99                        
        DataC        111                       
        DataC        110                       
        DataC        115                       
        DataC        116                       
        DataC        32                        
        DataC        112                       
        DataC        114                       
        DataC        105                       
        DataC        110                       
        DataC        116                       
        DataC        32                        
        DataC        92                        
        DataC        110                       
        DataC        0                         
        PushD        $string-constant-memory   
        PushI        0                         
        Add                                    
        StoreI                                 
        PushD        $global-memory-block      
        PushI        20                        
        Add                                    %% chr
        PushI        116                       
        StoreC                                 
        PushD        $global-memory-block      
        PushI        21                        
        Add                                    %% VArIble_$098
        PushF        -0.006100                 
        StoreF                                 
        PushD        $global-memory-block      
        PushI        0                         
        Add                                    %% quarters
        PushI        9                         
        PushI        5                         
        PushI        3                         
        Add                                    
        Add                                    
        StoreI                                 
        PushD        $global-memory-block      
        PushI        29                        
        Add                                    %% value
        PushD        $global-memory-block      
        PushI        0                         
        Add                                    %% quarters
        LoadI                                  
        PushI        25                        
        Duplicate                              
        JumpFalse    $$i-divide-by-zero        
        Divide                                 
        PushD        $global-memory-block      
        PushI        4                         
        Add                                    %% dimes
        LoadI                                  
        PushI        10                        
        Duplicate                              
        JumpFalse    $$i-divide-by-zero        
        Divide                                 
        Subtract                               
        PushD        $global-memory-block      
        PushI        8                         
        Add                                    %% nickels
        LoadI                                  
        PushI        5                         
        Duplicate                              
        JumpFalse    $$i-divide-by-zero        
        Divide                                 
        Add                                    
        PushD        $global-memory-block      
        PushI        12                        
        Add                                    %% pennies
        LoadI                                  
        Add                                    
        StoreI                                 
        PushD        $global-memory-block      
        PushI        29                        
        Add                                    %% value
        LoadI                                  
        PushD        $print-format-integer     
        Printf                                 
        PushD        $print-format-newline     
        Printf                                 
        PushD        $global-memory-block      
        PushI        33                        
        Add                                    %% dimes
        PushI        3                         
        StoreI                                 
        PushD        $global-memory-block      
        PushI        37                        
        Add                                    %% nickels
        PushI        7                         
        StoreI                                 
        PushD        $global-memory-block      
        PushI        41                        
        Add                                    %% pennies
        PushI        17                        
        StoreI                                 
        PushD        $global-memory-block      
        PushI        45                        
        Add                                    %% str
        DataC        104                       %% "hello kitty*const print \n"
        DataC        101                       
        DataC        108                       
        DataC        108                       
        DataC        111                       
        DataC        32                        
        DataC        107                       
        DataC        105                       
        DataC        116                       
        DataC        116                       
        DataC        121                       
        DataC        42                        
        DataC        99                        
        DataC        111                       
        DataC        110                       
        DataC        115                       
        DataC        116                       
        DataC        32                        
        DataC        112                       
        DataC        114                       
        DataC        105                       
        DataC        110                       
        DataC        116                       
        DataC        32                        
        DataC        92                        
        DataC        110                       
        DataC        0                         
        PushD        $string-constant-memory   
        PushI        27                        
        Add                                    
        StoreI                                 
        PushD        $global-memory-block      
        PushI        49                        
        Add                                    %% chr
        PushI        116                       
        StoreC                                 
        PushD        $global-memory-block      
        PushI        33                        
        Add                                    %% ncoins
        PushD        $global-memory-block      
        PushI        0                         
        Add                                    %% quarters
        LoadI                                  
        PushD        $global-memory-block      
        PushI        4                         
        Add                                    %% dimes
        LoadI                                  
        Add                                    
        PushD        $global-memory-block      
        PushI        8                         
        Add                                    %% nickels
        LoadI                                  
        Add                                    
        PushD        $global-memory-block      
        PushI        12                        
        Add                                    %% pennies
        LoadI                                  
        Add                                    
        StoreI                                 
        PushD        $global-memory-block      
        PushI        33                        
        Add                                    %% ncoins
        LoadI                                  
        PushD        $print-format-integer     
        Printf                                 
        PushD        $print-format-newline     
        Printf                                 
        PushD        $global-memory-block      
        PushI        37                        
        Add                                    %% moredimes
        Label        -compare-1-arg1           
        PushD        $global-memory-block      
        PushI        4                         
        Add                                    %% dimes
        LoadI                                  
        Label        -compare-1-arg2           
        PushD        $global-memory-block      
        PushI        8                         
        Add                                    %% nickels
        LoadI                                  
        Label        -compare-1-sub            
        Subtract                               
        JumpPos      -compare-1-false          
        Jump         -compare-1-true           
        Label        -compare-1-true           
        PushI        1                         
        Jump         -compare-1-join           
        Label        -compare-1-false          
        PushI        0                         
        Jump         -compare-1-join           
        Label        -compare-1-join           
        StoreC                                 
        PushD        $global-memory-block      
        PushI        37                        
        Add                                    %% moredimes
        LoadC                                  
        JumpTrue     -print-boolean-2-true     
        PushD        $boolean-false-string     
        Jump         -print-boolean-2-join     
        Label        -print-boolean-2-true     
        PushD        $boolean-true-string      
        Label        -print-boolean-2-join     
        PushD        $print-format-boolean     
        Printf                                 
        PushD        $print-format-newline     
        Printf                                 
        PushD        $global-memory-block      
        PushI        38                        
        Add                                    %% boot
        PushI        1                         
        StoreC                                 
        PushD        $global-memory-block      
        PushI        39                        
        Add                                    %% boof
        PushI        1                         
        StoreC                                 
        PushD        $global-memory-block      
        PushI        38                        
        Add                                    %% boot
        LoadC                                  
        JumpTrue     -print-boolean-3-true     
        PushD        $boolean-false-string     
        Jump         -print-boolean-3-join     
        Label        -print-boolean-3-true     
        PushD        $boolean-true-string      
        Label        -print-boolean-3-join     
        PushD        $print-format-boolean     
        Printf                                 
        PushD        $print-format-space       
        Printf                                 
        PushD        $global-memory-block      
        PushI        39                        
        Add                                    %% boof
        LoadC                                  
        JumpTrue     -print-boolean-4-true     
        PushD        $boolean-false-string     
        Jump         -print-boolean-4-join     
        Label        -print-boolean-4-true     
        PushD        $boolean-true-string      
        Label        -print-boolean-4-join     
        PushD        $print-format-boolean     
        Printf                                 
        PushD        $print-format-newline     
        Printf                                 
        PushD        $global-memory-block      
        PushI        38                        
        Add                                    %% boot
        LoadC                                  
        JumpTrue     -print-boolean-5-true     
        PushD        $boolean-false-string     
        Jump         -print-boolean-5-join     
        Label        -print-boolean-5-true     
        PushD        $boolean-true-string      
        Label        -print-boolean-5-join     
        PushD        $print-format-boolean     
        Printf                                 
        PushD        $global-memory-block      
        PushI        39                        
        Add                                    %% boof
        LoadC                                  
        JumpTrue     -print-boolean-6-true     
        PushD        $boolean-false-string     
        Jump         -print-boolean-6-join     
        Label        -print-boolean-6-true     
        PushD        $boolean-true-string      
        Label        -print-boolean-6-join     
        PushD        $print-format-boolean     
        Printf                                 
        PushD        $print-format-newline     
        Printf                                 
        PushD        $print-format-tab         
        Printf                                 
        Halt                                   
