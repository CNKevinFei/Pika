        Label        -mem-manager-initialize   
        DLabel       $heap-start-ptr           
        DataZ        4                         
        DLabel       $heap-after-ptr           
        DataZ        4                         
        DLabel       $heap-first-free          
        DataZ        4                         
        DLabel       $mmgr-newblock-block      
        DataZ        4                         
        DLabel       $mmgr-newblock-size       
        DataZ        4                         
        PushD        $heap-memory              
        Duplicate                              
        PushD        $heap-start-ptr           
        Exchange                               
        StoreI                                 
        PushD        $heap-after-ptr           
        Exchange                               
        StoreI                                 
        PushI        0                         
        PushD        $heap-first-free          
        Exchange                               
        StoreI                                 
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
        DLabel       $errors-float-divide-by-zero 
        DataC        102                       %% "float divide by zero"
        DataC        108                       
        DataC        111                       
        DataC        97                        
        DataC        116                       
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
        Label        $$f-divide-by-zero        
        PushD        $errors-float-divide-by-zero 
        Jump         $$general-runtime-error   
        DLabel       $usable-memory-start      
        DLabel       $global-memory-block      
        DataZ        4                         
        DLabel       $string-constant-memory   
        Label        $$main                    
        PushD        $global-memory-block      
        PushI        0                         
        Add                                    %% x
        PushI        1                         
        Duplicate                              
        PushI        4                         
        Multiply                               
        PushI        16                        
        Add                                    
        Call         -mem-manager-allocate     
        Exchange                               
        PushI        4                         
        Exchange                               
        PushI        0                         
        Call         -mem-store-array-header   
        PushI        0                         
        Call         -mem-store-array-four-byte 
        PushI        2                         
        Duplicate                              
        PushI        4                         
        Multiply                               
        PushI        16                        
        Add                                    
        Call         -mem-manager-allocate     
        Exchange                               
        PushI        4                         
        Exchange                               
        PushI        0                         
        Call         -mem-store-array-header   
        PushI        0                         
        Call         -mem-store-array-four-byte 
        PushI        16                        
        PushI        8                         
        Add                                    
        Call         -mem-manager-allocate     
        PushI        4                         
        PushI        2                         
        PushI        4                         
        Call         -mem-store-array-header   
        PushI        1                         
        Call         -mem-store-array-four-byte 
        StoreI                                 
        Halt                                   
        Label        -mem-manager-make-tags    
        DLabel       $mmgr-tags-size           
        DataZ        4                         
        DLabel       $mmgr-tags-start          
        DataZ        4                         
        DLabel       $mmgr-tags-available      
        DataZ        4                         
        DLabel       $mmgr-tags-nextptr        
        DataZ        4                         
        DLabel       $mmgr-tags-prevptr        
        DataZ        4                         
        DLabel       $mmgr-tags-return         
        DataZ        4                         
        PushD        $mmgr-tags-return         
        Exchange                               
        StoreI                                 
        PushD        $mmgr-tags-size           
        Exchange                               
        StoreI                                 
        PushD        $mmgr-tags-start          
        Exchange                               
        StoreI                                 
        PushD        $mmgr-tags-available      
        Exchange                               
        StoreI                                 
        PushD        $mmgr-tags-nextptr        
        Exchange                               
        StoreI                                 
        PushD        $mmgr-tags-prevptr        
        Exchange                               
        StoreI                                 
        PushD        $mmgr-tags-prevptr        
        LoadI                                  
        PushD        $mmgr-tags-size           
        LoadI                                  
        PushD        $mmgr-tags-available      
        LoadI                                  
        PushD        $mmgr-tags-start          
        LoadI                                  
        Call         -mem-manager-one-tag      
        PushD        $mmgr-tags-nextptr        
        LoadI                                  
        PushD        $mmgr-tags-size           
        LoadI                                  
        PushD        $mmgr-tags-available      
        LoadI                                  
        PushD        $mmgr-tags-start          
        LoadI                                  
        Duplicate                              
        PushI        4                         
        Add                                    
        LoadI                                  
        Add                                    
        PushI        9                         
        Subtract                               
        Call         -mem-manager-one-tag      
        PushD        $mmgr-tags-return         
        LoadI                                  
        Return                                 
        Label        -mem-manager-one-tag      
        DLabel       $mmgr-onetag-return       
        DataZ        4                         
        DLabel       $mmgr-onetag-location     
        DataZ        4                         
        DLabel       $mmgr-onetag-available    
        DataZ        4                         
        DLabel       $mmgr-onetag-size         
        DataZ        4                         
        DLabel       $mmgr-onetag-pointer      
        DataZ        4                         
        PushD        $mmgr-onetag-return       
        Exchange                               
        StoreI                                 
        PushD        $mmgr-onetag-location     
        Exchange                               
        StoreI                                 
        PushD        $mmgr-onetag-available    
        Exchange                               
        StoreI                                 
        PushD        $mmgr-onetag-size         
        Exchange                               
        StoreI                                 
        PushD        $mmgr-onetag-location     
        LoadI                                  
        PushI        0                         
        Add                                    
        Exchange                               
        StoreI                                 
        PushD        $mmgr-onetag-size         
        LoadI                                  
        PushD        $mmgr-onetag-location     
        LoadI                                  
        PushI        4                         
        Add                                    
        Exchange                               
        StoreI                                 
        PushD        $mmgr-onetag-available    
        LoadI                                  
        PushD        $mmgr-onetag-location     
        LoadI                                  
        PushI        8                         
        Add                                    
        Exchange                               
        StoreC                                 
        PushD        $mmgr-onetag-return       
        LoadI                                  
        Return                                 
        Label        -mem-manager-allocate     
        DLabel       $mmgr-alloc-return        
        DataZ        4                         
        DLabel       $mmgr-alloc-size          
        DataZ        4                         
        DLabel       $mmgr-alloc-current-block 
        DataZ        4                         
        DLabel       $mmgr-alloc-remainder-block 
        DataZ        4                         
        DLabel       $mmgr-alloc-remainder-size 
        DataZ        4                         
        PushD        $mmgr-alloc-return        
        Exchange                               
        StoreI                                 
        PushI        18                        
        Add                                    
        PushD        $mmgr-alloc-size          
        Exchange                               
        StoreI                                 
        PushD        $heap-first-free          
        LoadI                                  
        PushD        $mmgr-alloc-current-block 
        Exchange                               
        StoreI                                 
        Label        -mmgr-alloc-process-current 
        PushD        $mmgr-alloc-current-block 
        LoadI                                  
        JumpFalse    -mmgr-alloc-no-block-works 
        Label        -mmgr-alloc-test-block    
        PushD        $mmgr-alloc-current-block 
        LoadI                                  
        PushI        4                         
        Add                                    
        LoadI                                  
        PushD        $mmgr-alloc-size          
        LoadI                                  
        Subtract                               
        PushI        1                         
        Add                                    
        JumpPos      -mmgr-alloc-found-block   
        PushD        $mmgr-alloc-current-block 
        LoadI                                  
        Duplicate                              
        PushI        4                         
        Add                                    
        LoadI                                  
        Add                                    
        PushI        9                         
        Subtract                               
        PushI        0                         
        Add                                    
        LoadI                                  
        PushD        $mmgr-alloc-current-block 
        Exchange                               
        StoreI                                 
        Jump         -mmgr-alloc-process-current 
        Label        -mmgr-alloc-found-block   
        PushD        $mmgr-alloc-current-block 
        LoadI                                  
        Call         -mem-manager-remove-block 
        PushD        $mmgr-alloc-current-block 
        LoadI                                  
        PushI        4                         
        Add                                    
        LoadI                                  
        PushD        $mmgr-alloc-size          
        LoadI                                  
        Subtract                               
        PushI        26                        
        Subtract                               
        JumpNeg      -mmgr-alloc-return-userblock 
        PushD        $mmgr-alloc-current-block 
        LoadI                                  
        PushD        $mmgr-alloc-size          
        LoadI                                  
        Add                                    
        PushD        $mmgr-alloc-remainder-block 
        Exchange                               
        StoreI                                 
        PushD        $mmgr-alloc-size          
        LoadI                                  
        PushD        $mmgr-alloc-current-block 
        LoadI                                  
        PushI        4                         
        Add                                    
        LoadI                                  
        Exchange                               
        Subtract                               
        PushD        $mmgr-alloc-remainder-size 
        Exchange                               
        StoreI                                 
        PushI        0                         
        PushI        0                         
        PushI        0                         
        PushD        $mmgr-alloc-current-block 
        LoadI                                  
        PushD        $mmgr-alloc-size          
        LoadI                                  
        Call         -mem-manager-make-tags    
        PushI        0                         
        PushI        0                         
        PushI        1                         
        PushD        $mmgr-alloc-remainder-block 
        LoadI                                  
        PushD        $mmgr-alloc-remainder-size 
        LoadI                                  
        Call         -mem-manager-make-tags    
        PushD        $mmgr-alloc-remainder-block 
        LoadI                                  
        PushI        9                         
        Add                                    
        Call         -mem-manager-deallocate   
        Jump         -mmgr-alloc-return-userblock 
        Label        -mmgr-alloc-no-block-works 
        PushD        $mmgr-alloc-size          
        LoadI                                  
        PushD        $mmgr-newblock-size       
        Exchange                               
        StoreI                                 
        PushD        $heap-after-ptr           
        LoadI                                  
        PushD        $mmgr-newblock-block      
        Exchange                               
        StoreI                                 
        PushD        $mmgr-newblock-size       
        LoadI                                  
        PushD        $heap-after-ptr           
        LoadI                                  
        Add                                    
        PushD        $heap-after-ptr           
        Exchange                               
        StoreI                                 
        PushI        0                         
        PushI        0                         
        PushI        0                         
        PushD        $mmgr-newblock-block      
        LoadI                                  
        PushD        $mmgr-newblock-size       
        LoadI                                  
        Call         -mem-manager-make-tags    
        PushD        $mmgr-newblock-block      
        LoadI                                  
        PushD        $mmgr-alloc-current-block 
        Exchange                               
        StoreI                                 
        Label        -mmgr-alloc-return-userblock 
        PushD        $mmgr-alloc-current-block 
        LoadI                                  
        PushI        9                         
        Add                                    
        PushD        $mmgr-alloc-return        
        LoadI                                  
        Return                                 
        Label        -mem-manager-deallocate   
        DLabel       $mmgr-dealloc-return      
        DataZ        4                         
        DLabel       $mmgr-dealloc-block       
        DataZ        4                         
        PushD        $mmgr-dealloc-return      
        Exchange                               
        StoreI                                 
        PushI        9                         
        Subtract                               
        PushD        $mmgr-dealloc-block       
        Exchange                               
        StoreI                                 
        PushD        $mmgr-dealloc-block       
        LoadI                                  
        PushD        $heap-first-free          
        LoadI                                  
        PushI        0                         
        Add                                    
        Exchange                               
        StoreI                                 
        PushI        0                         
        PushD        $mmgr-dealloc-block       
        LoadI                                  
        PushI        0                         
        Add                                    
        Exchange                               
        StoreI                                 
        PushD        $heap-first-free          
        LoadI                                  
        PushD        $mmgr-dealloc-block       
        LoadI                                  
        Duplicate                              
        PushI        4                         
        Add                                    
        LoadI                                  
        Add                                    
        PushI        9                         
        Subtract                               
        PushI        0                         
        Add                                    
        Exchange                               
        StoreI                                 
        PushI        1                         
        PushD        $mmgr-dealloc-block       
        LoadI                                  
        PushI        8                         
        Add                                    
        Exchange                               
        StoreC                                 
        PushI        1                         
        PushD        $mmgr-dealloc-block       
        LoadI                                  
        Duplicate                              
        PushI        4                         
        Add                                    
        LoadI                                  
        Add                                    
        PushI        9                         
        Subtract                               
        PushI        8                         
        Add                                    
        Exchange                               
        StoreC                                 
        PushD        $mmgr-dealloc-block       
        LoadI                                  
        PushD        $heap-first-free          
        Exchange                               
        StoreI                                 
        PushD        $mmgr-dealloc-return      
        LoadI                                  
        Return                                 
        Label        -mem-manager-remove-block 
        DLabel       $mmgr-remove-return       
        DataZ        4                         
        DLabel       $mmgr-remove-block        
        DataZ        4                         
        DLabel       $mmgr-remove-prev         
        DataZ        4                         
        DLabel       $mmgr-remove-next         
        DataZ        4                         
        PushD        $mmgr-remove-return       
        Exchange                               
        StoreI                                 
        PushD        $mmgr-remove-block        
        Exchange                               
        StoreI                                 
        PushD        $mmgr-remove-block        
        LoadI                                  
        PushI        0                         
        Add                                    
        LoadI                                  
        PushD        $mmgr-remove-prev         
        Exchange                               
        StoreI                                 
        PushD        $mmgr-remove-block        
        LoadI                                  
        Duplicate                              
        PushI        4                         
        Add                                    
        LoadI                                  
        Add                                    
        PushI        9                         
        Subtract                               
        PushI        0                         
        Add                                    
        LoadI                                  
        PushD        $mmgr-remove-next         
        Exchange                               
        StoreI                                 
        Label        -mmgr-remove-process-prev 
        PushD        $mmgr-remove-prev         
        LoadI                                  
        JumpFalse    -mmgr-remove-no-prev      
        PushD        $mmgr-remove-next         
        LoadI                                  
        PushD        $mmgr-remove-prev         
        LoadI                                  
        Duplicate                              
        PushI        4                         
        Add                                    
        LoadI                                  
        Add                                    
        PushI        9                         
        Subtract                               
        PushI        0                         
        Add                                    
        Exchange                               
        StoreI                                 
        Jump         -mmgr-remove-process-next 
        Label        -mmgr-remove-no-prev      
        PushD        $mmgr-remove-next         
        LoadI                                  
        PushD        $heap-first-free          
        Exchange                               
        StoreI                                 
        Label        -mmgr-remove-process-next 
        PushD        $mmgr-remove-next         
        LoadI                                  
        JumpFalse    -mmgr-remove-done         
        PushD        $mmgr-remove-prev         
        LoadI                                  
        PushD        $mmgr-remove-next         
        LoadI                                  
        PushI        0                         
        Add                                    
        Exchange                               
        StoreI                                 
        Label        -mmgr-remove-done         
        PushD        $mmgr-remove-return       
        LoadI                                  
        Return                                 
        Label        -mem-store-string-header  
        DLabel       $mem-string-return        
        DataZ        4                         
        PushD        $mem-string-return        
        Exchange                               
        StoreI                                 
        Exchange                               
        Duplicate                              
        PushI        6                         
        StoreI                                 
        Duplicate                              
        PushI        4                         
        Add                                    
        PushI        9                         
        StoreI                                 
        PushI        8                         
        Add                                    
        Exchange                               
        StoreI                                 
        PushD        $mem-string-return        
        LoadI                                  
        Return                                 
        Label        -mem-store-array-header   
        DLabel       $mem-array-return         
        DataZ        4                         
        DLabel       $mem-store-array-block-header 
        DataZ        4                         
        DLabel       $mem-store-array-status-header 
        DataZ        4                         
        DLabel       $mem-store-array-length-header 
        DataZ        4                         
        DLabel       $mem-store-array-size-header 
        DataZ        4                         
        PushD        $mem-array-return         
        Exchange                               
        StoreI                                 
        PushD        $mem-store-array-status-header 
        Exchange                               
        StoreI                                 
        PushD        $mem-store-array-length-header 
        Exchange                               
        StoreI                                 
        PushD        $mem-store-array-size-header 
        Exchange                               
        StoreI                                 
        PushD        $mem-store-array-block-header 
        Exchange                               
        StoreI                                 
        PushD        $mem-store-array-block-header 
        LoadI                                  
        PushI        7                         
        StoreI                                 
        PushD        $mem-store-array-block-header 
        LoadI                                  
        PushI        4                         
        Add                                    
        PushD        $mem-store-array-status-header 
        LoadI                                  
        StoreI                                 
        PushD        $mem-store-array-block-header 
        LoadI                                  
        PushI        8                         
        Add                                    
        PushD        $mem-store-array-size-header 
        LoadI                                  
        StoreI                                 
        PushD        $mem-store-array-block-header 
        LoadI                                  
        PushI        12                        
        Add                                    
        PushD        $mem-store-array-length-header 
        LoadI                                  
        StoreI                                 
        PushD        $mem-store-array-block-header 
        LoadI                                  
        PushD        $mem-store-array-length-header 
        LoadI                                  
        PushD        $mem-array-return         
        LoadI                                  
        Return                                 
        Label        -mem-store-array-one-byte 
        DLabel       $mem-store-array-one-byte-return 
        DataZ        4                         
        DLabel       $mem-store-array-length   
        DataZ        4                         
        DLabel       $mem-store-array-block    
        DataZ        4                         
        DLabel       $mem-store-array-flag     
        DataZ        4                         
        PushD        $mem-store-array-one-byte-return 
        Exchange                               
        StoreI                                 
        PushD        $mem-store-array-flag     
        Exchange                               
        StoreI                                 
        PushD        $mem-store-array-length   
        Exchange                               
        StoreI                                 
        PushD        $mem-store-array-block    
        Exchange                               
        StoreI                                 
        Label        $mem-store-array-loop-one 
        PushD        $mem-store-array-length   
        LoadI                                  
        JumpFalse    $mem-store-array-loop-end-one 
        PushD        $mem-store-array-length   
        LoadI                                  
        PushI        1                         
        Subtract                               
        PushD        $mem-store-array-length   
        Exchange                               
        StoreI                                 
        PushD        $mem-store-array-length   
        LoadI                                  
        PushD        $mem-store-array-block    
        LoadI                                  
        PushI        16                        
        Add                                    
        Add                                    
        PushD        $mem-store-array-flag     
        LoadI                                  
        JumpFalse    -mem-store-array-empty-one 
        Exchange                               
        StoreC                                 
        Jump         $mem-store-array-loop-one 
        Label        -mem-store-array-empty-one 
        PushI        0                         
        StoreC                                 
        Jump         $mem-store-array-loop-one 
        Label        $mem-store-array-loop-end-one 
        PushD        $mem-store-array-block    
        LoadI                                  
        PushD        $mem-store-array-one-byte-return 
        LoadI                                  
        Return                                 
        Label        -mem-store-array-four-byte 
        DLabel       $mem-store-array-four-byte-return 
        DataZ        4                         
        PushD        $mem-store-array-four-byte-return 
        Exchange                               
        StoreI                                 
        PushD        $mem-store-array-flag     
        Exchange                               
        StoreI                                 
        PushD        $mem-store-array-length   
        Exchange                               
        StoreI                                 
        PushD        $mem-store-array-block    
        Exchange                               
        StoreI                                 
        Label        $mem-store-array-loop-four 
        PushD        $mem-store-array-length   
        LoadI                                  
        JumpFalse    $mem-store-array-loop-end-four 
        PushD        $mem-store-array-length   
        LoadI                                  
        PushI        1                         
        Subtract                               
        PushD        $mem-store-array-length   
        Exchange                               
        StoreI                                 
        PushD        $mem-store-array-length   
        LoadI                                  
        PushI        4                         
        Multiply                               
        PushD        $mem-store-array-block    
        LoadI                                  
        PushI        16                        
        Add                                    
        Add                                    
        PushD        $mem-store-array-flag     
        LoadI                                  
        JumpFalse    -mem-store-array-empty-four 
        Exchange                               
        StoreI                                 
        Jump         $mem-store-array-loop-four 
        Label        -mem-store-array-empty-four 
        PushI        0                         
        StoreI                                 
        Jump         $mem-store-array-loop-four 
        Label        $mem-store-array-loop-end-four 
        PushD        $mem-store-array-block    
        LoadI                                  
        PushD        $mem-store-array-four-byte-return 
        LoadI                                  
        Return                                 
        Label        -mem-store-array-eight-byte 
        DLabel       $mem-store-array-eight-byte-return 
        DataZ        4                         
        PushD        $mem-store-array-eight-byte-return 
        Exchange                               
        StoreI                                 
        PushD        $mem-store-array-flag     
        Exchange                               
        StoreI                                 
        PushD        $mem-store-array-length   
        Exchange                               
        StoreI                                 
        PushD        $mem-store-array-block    
        Exchange                               
        StoreI                                 
        Label        $mem-store-array-loop-eight 
        PushD        $mem-store-array-length   
        LoadI                                  
        JumpFalse    $mem-store-array-loop-end-eight 
        PushD        $mem-store-array-length   
        LoadI                                  
        PushI        1                         
        Subtract                               
        PushD        $mem-store-array-length   
        Exchange                               
        StoreI                                 
        PushD        $mem-store-array-length   
        LoadI                                  
        PushI        8                         
        Multiply                               
        PushD        $mem-store-array-block    
        LoadI                                  
        PushI        16                        
        Add                                    
        Add                                    
        PushD        $mem-store-array-flag     
        LoadI                                  
        JumpFalse    -mem-store-array-empty-eight 
        Exchange                               
        StoreF                                 
        Jump         $mem-store-array-loop-eight 
        Label        -mem-store-array-empty-eight 
        PushF        0.000000                  
        StoreF                                 
        Jump         $mem-store-array-loop-eight 
        Label        $mem-store-array-loop-end-eight 
        PushD        $mem-store-array-block    
        LoadI                                  
        PushD        $mem-store-array-eight-byte-return 
        LoadI                                  
        Return                                 
        DLabel       $heap-memory              
