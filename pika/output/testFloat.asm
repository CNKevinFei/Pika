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
        Memtop                                 
        Duplicate                              
        PushD        $frame-stack-pointer      
        Exchange                               
        StoreI                                 
        PushD        $stack-pointer            
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
        DataC        105                       %% "integer divided by zero"
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
        DataC        100                       
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
        DLabel       $errors-rat-divide-by-zero 
        DataC        114                       %% "rat divide by zero"
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
        Label        $$r-divide-by-zero        
        PushD        $errors-rat-divide-by-zero 
        Jump         $$general-runtime-error   
        DLabel       $errors-denominator-zero  
        DataC        100                       %% "denominator is zero"
        DataC        101                       
        DataC        110                       
        DataC        111                       
        DataC        109                       
        DataC        105                       
        DataC        110                       
        DataC        97                        
        DataC        116                       
        DataC        111                       
        DataC        114                       
        DataC        32                        
        DataC        105                       
        DataC        115                       
        DataC        32                        
        DataC        122                       
        DataC        101                       
        DataC        114                       
        DataC        111                       
        DataC        0                         
        Label        $$r-denominator-zero      
        PushD        $errors-denominator-zero  
        Jump         $$general-runtime-error   
        DLabel       $errors-index-neg         
        DataC        97                        %% "array index is negative."
        DataC        114                       
        DataC        114                       
        DataC        97                        
        DataC        121                       
        DataC        32                        
        DataC        105                       
        DataC        110                       
        DataC        100                       
        DataC        101                       
        DataC        120                       
        DataC        32                        
        DataC        105                       
        DataC        115                       
        DataC        32                        
        DataC        110                       
        DataC        101                       
        DataC        103                       
        DataC        97                        
        DataC        116                       
        DataC        105                       
        DataC        118                       
        DataC        101                       
        DataC        46                        
        DataC        0                         
        Label        $$a-index-negative        
        PushD        $errors-index-neg         
        Jump         $$general-runtime-error   
        DLabel       $errors-index-exceed      
        DataC        97                        %% "array index exceeds."
        DataC        114                       
        DataC        114                       
        DataC        97                        
        DataC        121                       
        DataC        32                        
        DataC        105                       
        DataC        110                       
        DataC        100                       
        DataC        101                       
        DataC        120                       
        DataC        32                        
        DataC        101                       
        DataC        120                       
        DataC        99                        
        DataC        101                       
        DataC        101                       
        DataC        100                       
        DataC        115                       
        DataC        46                        
        DataC        0                         
        Label        $$a-index-exceed          
        PushD        $errors-index-exceed      
        Jump         $$general-runtime-error   
        DLabel       $errors-record-error      
        DataC        97                        %% "array record is not valid."
        DataC        114                       
        DataC        114                       
        DataC        97                        
        DataC        121                       
        DataC        32                        
        DataC        114                       
        DataC        101                       
        DataC        99                        
        DataC        111                       
        DataC        114                       
        DataC        100                       
        DataC        32                        
        DataC        105                       
        DataC        115                       
        DataC        32                        
        DataC        110                       
        DataC        111                       
        DataC        116                       
        DataC        32                        
        DataC        118                       
        DataC        97                        
        DataC        108                       
        DataC        105                       
        DataC        100                       
        DataC        46                        
        DataC        0                         
        DLabel       $errors-record-deleted-error 
        DataC        97                        %% "array record has been deleted."
        DataC        114                       
        DataC        114                       
        DataC        97                        
        DataC        121                       
        DataC        32                        
        DataC        114                       
        DataC        101                       
        DataC        99                        
        DataC        111                       
        DataC        114                       
        DataC        100                       
        DataC        32                        
        DataC        104                       
        DataC        97                        
        DataC        115                       
        DataC        32                        
        DataC        98                        
        DataC        101                       
        DataC        101                       
        DataC        110                       
        DataC        32                        
        DataC        100                       
        DataC        101                       
        DataC        108                       
        DataC        101                       
        DataC        116                       
        DataC        101                       
        DataC        100                       
        DataC        46                        
        DataC        0                         
        DLabel       $errors-string-error      
        DataC        115                       %% "string record is not valid."
        DataC        116                       
        DataC        114                       
        DataC        105                       
        DataC        110                       
        DataC        103                       
        DataC        32                        
        DataC        114                       
        DataC        101                       
        DataC        99                        
        DataC        111                       
        DataC        114                       
        DataC        100                       
        DataC        32                        
        DataC        105                       
        DataC        115                       
        DataC        32                        
        DataC        110                       
        DataC        111                       
        DataC        116                       
        DataC        32                        
        DataC        118                       
        DataC        97                        
        DataC        108                       
        DataC        105                       
        DataC        100                       
        DataC        46                        
        DataC        0                         
        Label        $$a-record-error          
        PushD        $errors-record-error      
        Jump         $$general-runtime-error   
        Label        $$a-record-deleted-error  
        PushD        $errors-record-deleted-error 
        Jump         $$general-runtime-error   
        Label        $$a-string-error          
        PushD        $errors-record-deleted-error 
        Jump         $$general-runtime-error   
        DLabel       $no-retuirn-error         
        DataC        119                       %% "without return statement."
        DataC        105                       
        DataC        116                       
        DataC        104                       
        DataC        111                       
        DataC        117                       
        DataC        116                       
        DataC        32                        
        DataC        114                       
        DataC        101                       
        DataC        116                       
        DataC        117                       
        DataC        114                       
        DataC        110                       
        DataC        32                        
        DataC        115                       
        DataC        116                       
        DataC        97                        
        DataC        116                       
        DataC        101                       
        DataC        109                       
        DataC        101                       
        DataC        110                       
        DataC        116                       
        DataC        46                        
        DataC        0                         
        Label        $$function-without-return 
        PushD        $no-retuirn-error         
        Jump         $$general-runtime-error   
        DLabel       $usable-memory-start      
        DLabel       $global-memory-block      
        DataZ        4                         
        DLabel       $frame-stack-pointer      
        DataZ        4                         
        DLabel       $stack-pointer            
        DataZ        4                         
        DLabel       $string-constant-memory   
        Label        $$main                    
        PushD        $global-memory-block      
        PushI        0                         
        Add                                    %% hello
        PushPC                                 
        PushI        3                         
        Add                                    
        Jump         -function-1-skip          
        PushD        $stack-pointer            
        LoadI                                  
        PushI        -4                        
        Add                                    
        Exchange                               
        StoreI                                 
        PushD        $stack-pointer            
        LoadI                                  
        PushI        -8                        
        Add                                    
        PushD        $frame-stack-pointer      
        LoadI                                  
        StoreI                                 
        PushD        $frame-stack-pointer      
        PushD        $stack-pointer            
        LoadI                                  
        StoreI                                 
        PushD        $stack-pointer            
        PushD        $frame-stack-pointer      
        LoadI                                  
        PushI        -8                        
        Add                                    
        StoreI                                 
        Jump         $$function-without-return 
        Label        -function-1-skip          
        StoreI                                 
        PushD        $global-memory-block      
        PushI        0                         
        Add                                    %% hello
        LoadI                                  
        CallV                                  
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
        Label        -mem-array-release        
        DLabel       $mem-array-release-return-address 
        DataZ        4                         
        DLabel       $mem-array-release-array-address 
        DataZ        4                         
        DLabel       $mem-array-release-array-length 
        DataZ        4                         
        PushD        $mem-array-release-return-address 
        Exchange                               
        StoreI                                 
        PushD        $mem-array-release-array-address 
        Exchange                               
        StoreI                                 
        PushD        $mem-array-release-array-address 
        LoadI                                  
        Call         -mem-arrya-record-valid   
        PushD        $mem-array-release-array-address 
        LoadI                                  
        PushI        4                         
        Add                                    
        LoadI                                  
        PushI        2                         
        Add                                    
        PushD        $mem-array-release-array-address 
        LoadI                                  
        PushI        4                         
        Add                                    
        Exchange                               
        StoreI                                 
        PushD        $mem-array-release-array-address 
        LoadI                                  
        PushD        $mem-array-release-array-address 
        LoadI                                  
        PushI        4                         
        Add                                    
        LoadI                                  
        PushI        4                         
        BTAnd                                  
        JumpFalse    $mem-array-release-not-ref 
        PushD        $mem-array-release-array-address 
        LoadI                                  
        PushI        12                        
        Add                                    
        LoadI                                  
        PushD        $mem-array-release-array-length 
        Exchange                               
        StoreI                                 
        Label        $mem-array-release-loop   
        PushD        $mem-array-release-array-length 
        LoadI                                  
        JumpFalse    $mem-array-release-end    
        PushD        $mem-array-release-array-length 
        LoadI                                  
        PushI        1                         
        Subtract                               
        PushD        $mem-array-release-array-length 
        Exchange                               
        StoreI                                 
        PushD        $mem-array-release-array-length 
        LoadI                                  
        PushD        $mem-array-release-array-address 
        LoadI                                  
        PushI        8                         
        Add                                    
        LoadI                                  
        Multiply                               
        PushI        16                        
        PushD        $mem-array-release-array-address 
        LoadI                                  
        Add                                    
        Add                                    
        LoadI                                  
        PushD        $mem-array-release-return-address 
        LoadI                                  
        PushD        $mem-array-release-array-address 
        LoadI                                  
        PushD        $mem-array-release-array-length 
        LoadI                                  
        Call         -mem-array-release        
        PushD        $mem-array-release-array-length 
        Exchange                               
        StoreI                                 
        PushD        $mem-array-release-array-address 
        Exchange                               
        StoreI                                 
        PushD        $mem-array-release-return-address 
        Exchange                               
        StoreI                                 
        Jump         $mem-array-release-loop   
        Label        $mem-array-release-not-ref 
        PushD        $mem-array-release-array-address 
        LoadI                                  
        Call         -mem-manager-deallocate   
        Label        $mem-array-release-end    
        PushD        $mem-array-release-return-address 
        LoadI                                  
        Return                                 
        Label        -mem-array-clone          
        DLabel       $mem-array-clone-return-address 
        DataZ        4                         
        DLabel       $mem-array-clone-array-address 
        DataZ        4                         
        DLabel       $mem-array-clone-result-address 
        DataZ        4                         
        DLabel       $mem-array-clone-record-size 
        DataZ        4                         
        PushD        $mem-array-clone-return-address 
        Exchange                               
        StoreI                                 
        PushD        $mem-array-clone-array-address 
        Exchange                               
        StoreI                                 
        PushD        $mem-array-clone-array-address 
        LoadI                                  
        PushI        12                        
        Add                                    
        LoadI                                  
        PushD        $mem-array-clone-array-address 
        LoadI                                  
        PushI        8                         
        Add                                    
        LoadI                                  
        Multiply                               
        PushI        16                        
        Add                                    
        PushD        $mem-array-clone-record-size 
        Exchange                               
        StoreI                                 
        PushD        $mem-array-clone-record-size 
        LoadI                                  
        Call         -mem-manager-allocate     
        PushD        $mem-array-clone-result-address 
        Exchange                               
        StoreI                                 
        Label        $mem-array-clone-loop     
        PushD        $mem-array-clone-record-size 
        LoadI                                  
        JumpFalse    $mem-array-clone-end      
        PushD        $mem-array-clone-record-size 
        LoadI                                  
        PushI        1                         
        Subtract                               
        PushD        $mem-array-clone-record-size 
        Exchange                               
        StoreI                                 
        PushD        $mem-array-clone-record-size 
        LoadI                                  
        PushD        $mem-array-clone-array-address 
        LoadI                                  
        Add                                    
        LoadC                                  
        PushD        $mem-array-clone-record-size 
        LoadI                                  
        PushD        $mem-array-clone-result-address 
        LoadI                                  
        Add                                    
        Exchange                               
        StoreC                                 
        Jump         $mem-array-clone-loop     
        Label        $mem-array-clone-end      
        PushD        $mem-array-clone-result-address 
        LoadI                                  
        PushD        $mem-array-clone-return-address 
        LoadI                                  
        Return                                 
        Label        -mem-array-index          
        DLabel       $mem-array-index-return-address 
        DataZ        4                         
        DLabel       $mem-array-index-array-address 
        DataZ        4                         
        DLabel       $mem-array-index-num      
        DataZ        4                         
        DLabel       $mem-array-index-size     
        DataZ        4                         
        DLabel       $mem-array-index-flag     
        DataZ        4                         
        PushD        $mem-array-index-return-address 
        Exchange                               
        StoreI                                 
        PushD        $mem-array-index-flag     
        Exchange                               
        StoreI                                 
        PushD        $mem-array-index-num      
        Exchange                               
        StoreI                                 
        PushD        $mem-array-index-array-address 
        Exchange                               
        StoreI                                 
        PushD        $mem-array-index-num      
        LoadI                                  
        JumpNeg      $$a-index-negative        
        PushD        $mem-array-index-array-address 
        LoadI                                  
        PushI        12                        
        Add                                    
        LoadI                                  
        PushI        1                         
        Subtract                               
        PushD        $mem-array-index-num      
        LoadI                                  
        Subtract                               
        JumpNeg      $$a-index-exceed          
        PushD        $mem-array-index-array-address 
        LoadI                                  
        PushI        8                         
        Add                                    
        LoadI                                  
        PushD        $mem-array-index-size     
        Exchange                               
        StoreI                                 
        PushD        $mem-array-index-size     
        LoadI                                  
        PushD        $mem-array-index-num      
        LoadI                                  
        Multiply                               
        PushI        16                        
        PushD        $mem-array-index-array-address 
        LoadI                                  
        Add                                    
        Add                                    
        PushD        $mem-array-index-return-address 
        LoadI                                  
        Return                                 
        Label        -mem-rat-GCD              
        DLabel       $mem-rat-gcd-return-address 
        DataZ        4                         
        DLabel       $mem-rat-gcd-numerator    
        DataZ        4                         
        DLabel       $mem-rat-gcd-denominator  
        DataZ        4                         
        DLabel       $mem-rat-gcd-a            
        DataZ        4                         
        DLabel       $mem-rat-gcd-b            
        DataZ        4                         
        PushD        $mem-rat-gcd-return-address 
        Exchange                               
        StoreI                                 
        PushD        $mem-rat-gcd-denominator  
        Exchange                               
        StoreI                                 
        PushD        $mem-rat-gcd-numerator    
        Exchange                               
        StoreI                                 
        PushD        $mem-rat-gcd-numerator    
        LoadI                                  
        PushD        $mem-rat-gcd-a            
        Exchange                               
        StoreI                                 
        PushD        $mem-rat-gcd-denominator  
        LoadI                                  
        PushD        $mem-rat-gcd-b            
        Exchange                               
        StoreI                                 
        Label        $mem-rat-gcd-loop         
        PushD        $mem-rat-gcd-b            
        LoadI                                  
        JumpFalse    $mem-rat-gcd-end          
        PushD        $mem-rat-gcd-b            
        LoadI                                  
        PushD        $mem-rat-gcd-a            
        LoadI                                  
        PushD        $mem-rat-gcd-b            
        LoadI                                  
        Remainder                              
        PushD        $mem-rat-gcd-b            
        Exchange                               
        StoreI                                 
        PushD        $mem-rat-gcd-a            
        Exchange                               
        StoreI                                 
        Jump         $mem-rat-gcd-loop         
        Label        $mem-rat-gcd-end          
        PushD        $mem-rat-gcd-numerator    
        LoadI                                  
        PushD        $mem-rat-gcd-a            
        LoadI                                  
        Divide                                 
        JumpNeg      $mem-rat-gcd-neg          
        PushD        $mem-rat-gcd-numerator    
        LoadI                                  
        PushD        $mem-rat-gcd-a            
        LoadI                                  
        Divide                                 
        PushD        $mem-rat-gcd-denominator  
        LoadI                                  
        PushD        $mem-rat-gcd-a            
        LoadI                                  
        Divide                                 
        PushD        $mem-rat-gcd-return-address 
        LoadI                                  
        Return                                 
        Label        $mem-rat-gcd-neg          
        PushD        $mem-rat-gcd-numerator    
        LoadI                                  
        PushD        $mem-rat-gcd-a            
        LoadI                                  
        Divide                                 
        Negate                                 
        PushD        $mem-rat-gcd-denominator  
        LoadI                                  
        PushD        $mem-rat-gcd-a            
        LoadI                                  
        Divide                                 
        Negate                                 
        PushD        $mem-rat-gcd-return-address 
        LoadI                                  
        Return                                 
        Label        -mem-rat-store            
        DLabel       $mem-rat-store-return-address 
        DataZ        4                         
        DLabel       $mem-rat-store-numerator  
        DataZ        4                         
        DLabel       $mem-rat-store-denominator 
        DataZ        4                         
        DLabel       $mem-rat-store-target-address 
        DataZ        4                         
        PushD        $mem-rat-store-return-address 
        Exchange                               
        StoreI                                 
        PushD        $mem-rat-store-denominator 
        Exchange                               
        StoreI                                 
        PushD        $mem-rat-store-numerator  
        Exchange                               
        StoreI                                 
        PushD        $mem-rat-store-target-address 
        Exchange                               
        StoreI                                 
        PushD        $mem-rat-store-target-address 
        LoadI                                  
        PushD        $mem-rat-store-numerator  
        LoadI                                  
        StoreI                                 
        PushD        $mem-rat-store-target-address 
        LoadI                                  
        PushI        4                         
        Add                                    
        PushD        $mem-rat-store-denominator 
        LoadI                                  
        StoreI                                 
        PushD        $mem-rat-store-return-address 
        LoadI                                  
        Return                                 
        Label        -mem-rat-aid              
        DLabel       $mem-rat-aid-return-address 
        DataZ        4                         
        DLabel       $mem-rat-aid-a            
        DataZ        4                         
        DLabel       $mem-rat-aid-b            
        DataZ        4                         
        PushD        $mem-rat-aid-return-address 
        Exchange                               
        StoreI                                 
        PushD        $mem-rat-aid-b            
        Exchange                               
        StoreI                                 
        PushD        $mem-rat-aid-a            
        Exchange                               
        StoreI                                 
        PushD        $mem-rat-aid-a            
        LoadI                                  
        PushD        $mem-rat-aid-b            
        LoadI                                  
        Divide                                 
        PushD        $mem-rat-aid-b            
        LoadI                                  
        PushD        $mem-rat-aid-return-address 
        LoadI                                  
        Return                                 
        Label        -mem-rat-print            
        DLabel       $mem-rat-print-return-address 
        DataZ        4                         
        DLabel       $mem-rat-print-a          
        DataZ        4                         
        DLabel       $mem-rat-print-b          
        DataZ        4                         
        PushD        $mem-rat-print-return-address 
        Exchange                               
        StoreI                                 
        PushD        $mem-rat-print-b          
        Exchange                               
        StoreI                                 
        PushD        $mem-rat-print-a          
        Exchange                               
        StoreI                                 
        PushD        $mem-rat-print-a          
        LoadI                                  
        JumpFalse    $mem-rat-print-zero       
        PushD        $mem-rat-print-a          
        LoadI                                  
        PushD        $mem-rat-print-b          
        LoadI                                  
        Divide                                 
        JumpFalse    $mem-rat-print-neg        
        PushD        $mem-rat-print-a          
        LoadI                                  
        PushD        $mem-rat-print-b          
        LoadI                                  
        Divide                                 
        PushD        $print-format-integer     
        Printf                                 
        Label        $mem-rat-print-rat        
        PushD        $mem-rat-print-a          
        LoadI                                  
        PushD        $mem-rat-print-b          
        LoadI                                  
        Remainder                              
        JumpFalse    $mem-rat-print-end        
        PushI        95                        
        PushD        $print-format-char        
        Printf                                 
        PushD        $mem-rat-print-a          
        LoadI                                  
        PushD        $mem-rat-print-b          
        LoadI                                  
        Remainder                              
        PushD        $print-format-integer     
        Printf                                 
        PushI        47                        
        PushD        $print-format-char        
        Printf                                 
        PushD        $mem-rat-print-b          
        LoadI                                  
        PushD        $mem-rat-print-b          
        LoadI                                  
        JumpPos      $mem-rat-print-con        
        Negate                                 
        Label        $mem-rat-print-con        
        PushD        $print-format-integer     
        Printf                                 
        Jump         $mem-rat-print-end        
        Label        $mem-rat-print-zero       
        PushI        0                         
        PushD        $print-format-integer     
        Printf                                 
        Label        $mem-rat-print-end        
        PushD        $mem-rat-print-return-address 
        LoadI                                  
        Return                                 
        Label        $mem-rat-print-neg        
        PushD        $mem-rat-print-b          
        LoadI                                  
        JumpPos      $mem-rat-print-rat        
        PushI        45                        
        PushD        $print-format-char        
        Printf                                 
        Jump         $mem-rat-print-rat        
        Label        -mem-rat-add              
        DLabel       $mem-rat-add-return-address 
        DataZ        4                         
        DLabel       $mem-rat-add-a-num        
        DataZ        4                         
        DLabel       $mem-rat-add-a-den        
        DataZ        4                         
        DLabel       $mem-rat-add-b-num        
        DataZ        4                         
        DLabel       $mem-rat-add-b-den        
        DataZ        4                         
        PushD        $mem-rat-add-return-address 
        Exchange                               
        StoreI                                 
        PushD        $mem-rat-add-b-den        
        Exchange                               
        StoreI                                 
        PushD        $mem-rat-add-b-num        
        Exchange                               
        StoreI                                 
        PushD        $mem-rat-add-a-den        
        Exchange                               
        StoreI                                 
        PushD        $mem-rat-add-a-num        
        Exchange                               
        StoreI                                 
        PushD        $mem-rat-add-a-num        
        LoadI                                  
        PushD        $mem-rat-add-b-den        
        LoadI                                  
        Multiply                               
        PushD        $mem-rat-add-b-num        
        LoadI                                  
        PushD        $mem-rat-add-a-den        
        LoadI                                  
        Multiply                               
        Add                                    
        PushD        $mem-rat-add-a-den        
        LoadI                                  
        PushD        $mem-rat-add-b-den        
        LoadI                                  
        Multiply                               
        Call         -mem-rat-GCD              
        PushD        $mem-rat-add-return-address 
        LoadI                                  
        Return                                 
        Label        -mem-rat-subtract         
        DLabel       $mem-rat-subtract-return-address 
        DataZ        4                         
        DLabel       $mem-rat-subtract-a-num   
        DataZ        4                         
        DLabel       $mem-rat-subtract-a-den   
        DataZ        4                         
        DLabel       $mem-rat-subtract-b-num   
        DataZ        4                         
        DLabel       $mem-rat-subtract-b-den   
        DataZ        4                         
        PushD        $mem-rat-subtract-return-address 
        Exchange                               
        StoreI                                 
        PushD        $mem-rat-subtract-b-den   
        Exchange                               
        StoreI                                 
        PushD        $mem-rat-subtract-b-num   
        Exchange                               
        StoreI                                 
        PushD        $mem-rat-subtract-a-den   
        Exchange                               
        StoreI                                 
        PushD        $mem-rat-subtract-a-num   
        Exchange                               
        StoreI                                 
        PushD        $mem-rat-subtract-a-num   
        LoadI                                  
        PushD        $mem-rat-subtract-b-den   
        LoadI                                  
        Multiply                               
        PushD        $mem-rat-subtract-b-num   
        LoadI                                  
        PushD        $mem-rat-subtract-a-den   
        LoadI                                  
        Multiply                               
        Subtract                               
        PushD        $mem-rat-subtract-a-den   
        LoadI                                  
        PushD        $mem-rat-subtract-b-den   
        LoadI                                  
        Multiply                               
        Call         -mem-rat-GCD              
        PushD        $mem-rat-subtract-return-address 
        LoadI                                  
        Return                                 
        Label        -mem-rat-multiply         
        DLabel       $mem-rat-multiply-return-address 
        DataZ        4                         
        DLabel       $mem-rat-multiply-a-num   
        DataZ        4                         
        DLabel       $mem-rat-multiply-a-den   
        DataZ        4                         
        DLabel       $mem-rat-multiply-b-num   
        DataZ        4                         
        DLabel       $mem-rat-multiply-b-den   
        DataZ        4                         
        PushD        $mem-rat-multiply-return-address 
        Exchange                               
        StoreI                                 
        PushD        $mem-rat-multiply-b-den   
        Exchange                               
        StoreI                                 
        PushD        $mem-rat-multiply-b-num   
        Exchange                               
        StoreI                                 
        PushD        $mem-rat-multiply-a-den   
        Exchange                               
        StoreI                                 
        PushD        $mem-rat-multiply-a-num   
        Exchange                               
        StoreI                                 
        PushD        $mem-rat-multiply-a-num   
        LoadI                                  
        PushD        $mem-rat-multiply-b-num   
        LoadI                                  
        Multiply                               
        PushD        $mem-rat-multiply-a-den   
        LoadI                                  
        PushD        $mem-rat-multiply-b-den   
        LoadI                                  
        Multiply                               
        Call         -mem-rat-GCD              
        PushD        $mem-rat-multiply-return-address 
        LoadI                                  
        Return                                 
        Label        -mem-rat-divide           
        DLabel       $mem-rat-divide-return-address 
        DataZ        4                         
        DLabel       $mem-rat-divide-a-num     
        DataZ        4                         
        DLabel       $mem-rat-divide-a-den     
        DataZ        4                         
        DLabel       $mem-rat-divide-b-num     
        DataZ        4                         
        DLabel       $mem-rat-divide-b-den     
        DataZ        4                         
        PushD        $mem-rat-divide-return-address 
        Exchange                               
        StoreI                                 
        PushD        $mem-rat-divide-b-den     
        Exchange                               
        StoreI                                 
        PushD        $mem-rat-divide-b-num     
        Exchange                               
        StoreI                                 
        PushD        $mem-rat-divide-a-den     
        Exchange                               
        StoreI                                 
        PushD        $mem-rat-divide-a-num     
        Exchange                               
        StoreI                                 
        PushD        $mem-rat-divide-b-num     
        LoadI                                  
        JumpFalse    $$r-divide-by-zero        
        PushD        $mem-rat-divide-a-num     
        LoadI                                  
        PushD        $mem-rat-divide-b-den     
        LoadI                                  
        Multiply                               
        PushD        $mem-rat-divide-a-den     
        LoadI                                  
        PushD        $mem-rat-divide-b-num     
        LoadI                                  
        Multiply                               
        Call         -mem-rat-GCD              
        PushD        $mem-rat-divide-return-address 
        LoadI                                  
        Return                                 
        Label        -mem-array-int-print      
        DLabel       $mem-array-int-return-address 
        DataZ        4                         
        DLabel       $mem-array-int-array-address 
        DataZ        4                         
        DLabel       $mem-array-int-end-array-address 
        DataZ        4                         
        DLabel       $mem-array-int-length     
        DataZ        4                         
        DLabel       $mem-array-int-size       
        DataZ        4                         
        PushD        $mem-array-int-return-address 
        Exchange                               
        StoreI                                 
        PushD        $mem-array-int-array-address 
        Exchange                               
        StoreI                                 
        PushD        $mem-array-int-array-address 
        LoadI                                  
        Call         -mem-arrya-record-valid   
        PushD        $mem-array-int-array-address 
        LoadI                                  
        PushI        4                         
        Add                                    
        LoadI                                  
        PushI        91                        
        PushD        $print-format-char        
        Printf                                 
        PushD        $mem-array-int-array-address 
        LoadI                                  
        PushI        12                        
        Add                                    
        LoadI                                  
        PushD        $mem-array-int-length     
        Exchange                               
        StoreI                                 
        PushD        $mem-array-int-array-address 
        LoadI                                  
        PushI        8                         
        Add                                    
        LoadI                                  
        PushD        $mem-array-int-size       
        Exchange                               
        StoreI                                 
        PushD        $mem-array-int-array-address 
        LoadI                                  
        PushI        16                        
        PushD        $mem-array-int-length     
        LoadI                                  
        PushD        $mem-array-int-size       
        LoadI                                  
        Multiply                               
        Add                                    
        Add                                    
        PushD        $mem-array-int-end-array-address 
        Exchange                               
        StoreI                                 
        JumpPos      $mem-array-int-array      
        Label        $mem-array-int-loop       
        PushD        $mem-array-int-length     
        LoadI                                  
        JumpFalse    $mem-array-int-print-end  
        PushD        $mem-array-int-end-array-address 
        LoadI                                  
        PushD        $mem-array-int-length     
        LoadI                                  
        PushD        $mem-array-int-size       
        LoadI                                  
        Multiply                               
        Subtract                               
        LoadI                                  
        PushD        $print-format-integer     
        Printf                                 
        PushD        $mem-array-int-length     
        LoadI                                  
        PushI        1                         
        Subtract                               
        PushD        $mem-array-int-length     
        Exchange                               
        StoreI                                 
        PushD        $mem-array-int-length     
        LoadI                                  
        JumpFalse    $mem-array-int-loop       
        PushI        44                        
        PushD        $print-format-char        
        Printf                                 
        PushI        32                        
        PushD        $print-format-char        
        Printf                                 
        Jump         $mem-array-int-loop       
        Label        $mem-array-int-array      
        PushD        $mem-array-int-length     
        LoadI                                  
        JumpFalse    $mem-array-int-print-end  
        PushD        $mem-array-int-return-address 
        LoadI                                  
        PushD        $mem-array-int-array-address 
        LoadI                                  
        PushD        $mem-array-int-end-array-address 
        LoadI                                  
        PushD        $mem-array-int-length     
        LoadI                                  
        PushD        $mem-array-int-size       
        LoadI                                  
        PushD        $mem-array-int-end-array-address 
        LoadI                                  
        PushD        $mem-array-int-length     
        LoadI                                  
        PushD        $mem-array-int-size       
        LoadI                                  
        Multiply                               
        Subtract                               
        LoadI                                  
        Duplicate                              
        JumpFalse    $$a-record-error          
        Call         -mem-array-int-print      
        PushD        $mem-array-int-size       
        Exchange                               
        StoreI                                 
        PushD        $mem-array-int-length     
        Exchange                               
        StoreI                                 
        PushD        $mem-array-int-end-array-address 
        Exchange                               
        StoreI                                 
        PushD        $mem-array-int-array-address 
        Exchange                               
        StoreI                                 
        PushD        $mem-array-int-return-address 
        Exchange                               
        StoreI                                 
        PushD        $mem-array-int-length     
        LoadI                                  
        PushI        1                         
        Subtract                               
        PushD        $mem-array-int-length     
        Exchange                               
        StoreI                                 
        PushD        $mem-array-int-length     
        LoadI                                  
        JumpFalse    $mem-array-int-array      
        PushI        44                        
        PushD        $print-format-char        
        Printf                                 
        PushI        32                        
        PushD        $print-format-char        
        Printf                                 
        Jump         $mem-array-int-array      
        Label        $mem-array-int-print-end  
        PushI        93                        
        PushD        $print-format-char        
        Printf                                 
        PushD        $mem-array-int-return-address 
        LoadI                                  
        Return                                 
        Label        -mem-array-float-print    
        DLabel       $mem-array-float-return-address 
        DataZ        4                         
        DLabel       $mem-array-float-array-address 
        DataZ        4                         
        DLabel       $mem-array-float-end-array-address 
        DataZ        4                         
        DLabel       $mem-array-float-length   
        DataZ        4                         
        DLabel       $mem-array-float-size     
        DataZ        4                         
        PushD        $mem-array-float-return-address 
        Exchange                               
        StoreI                                 
        PushD        $mem-array-float-array-address 
        Exchange                               
        StoreI                                 
        PushD        $mem-array-float-array-address 
        LoadI                                  
        Call         -mem-arrya-record-valid   
        PushD        $mem-array-float-array-address 
        LoadI                                  
        PushI        4                         
        Add                                    
        LoadI                                  
        PushI        91                        
        PushD        $print-format-char        
        Printf                                 
        PushD        $mem-array-float-array-address 
        LoadI                                  
        PushI        12                        
        Add                                    
        LoadI                                  
        PushD        $mem-array-float-length   
        Exchange                               
        StoreI                                 
        PushD        $mem-array-float-array-address 
        LoadI                                  
        PushI        8                         
        Add                                    
        LoadI                                  
        PushD        $mem-array-float-size     
        Exchange                               
        StoreI                                 
        PushD        $mem-array-float-array-address 
        LoadI                                  
        PushI        16                        
        PushD        $mem-array-float-length   
        LoadI                                  
        PushD        $mem-array-float-size     
        LoadI                                  
        Multiply                               
        Add                                    
        Add                                    
        PushD        $mem-array-float-end-array-address 
        Exchange                               
        StoreI                                 
        JumpPos      $mem-array-float-array    
        Label        $mem-array-float-loop     
        PushD        $mem-array-float-length   
        LoadI                                  
        JumpFalse    $mem-array-float-print-end 
        PushD        $mem-array-float-end-array-address 
        LoadI                                  
        PushD        $mem-array-float-length   
        LoadI                                  
        PushD        $mem-array-float-size     
        LoadI                                  
        Multiply                               
        Subtract                               
        LoadF                                  
        PushD        $print-format-float       
        Printf                                 
        PushD        $mem-array-float-length   
        LoadI                                  
        PushI        1                         
        Subtract                               
        PushD        $mem-array-float-length   
        Exchange                               
        StoreI                                 
        PushD        $mem-array-float-length   
        LoadI                                  
        JumpFalse    $mem-array-float-loop     
        PushI        44                        
        PushD        $print-format-char        
        Printf                                 
        PushI        32                        
        PushD        $print-format-char        
        Printf                                 
        Jump         $mem-array-float-loop     
        Label        $mem-array-float-array    
        PushD        $mem-array-float-length   
        LoadI                                  
        JumpFalse    $mem-array-float-print-end 
        PushD        $mem-array-float-return-address 
        LoadI                                  
        PushD        $mem-array-float-array-address 
        LoadI                                  
        PushD        $mem-array-float-end-array-address 
        LoadI                                  
        PushD        $mem-array-float-length   
        LoadI                                  
        PushD        $mem-array-float-size     
        LoadI                                  
        PushD        $mem-array-float-end-array-address 
        LoadI                                  
        PushD        $mem-array-float-length   
        LoadI                                  
        PushD        $mem-array-float-size     
        LoadI                                  
        Multiply                               
        Subtract                               
        LoadI                                  
        Duplicate                              
        JumpFalse    $$a-record-error          
        Call         -mem-array-float-print    
        PushD        $mem-array-float-size     
        Exchange                               
        StoreI                                 
        PushD        $mem-array-float-length   
        Exchange                               
        StoreI                                 
        PushD        $mem-array-float-end-array-address 
        Exchange                               
        StoreI                                 
        PushD        $mem-array-float-array-address 
        Exchange                               
        StoreI                                 
        PushD        $mem-array-float-return-address 
        Exchange                               
        StoreI                                 
        PushD        $mem-array-float-length   
        LoadI                                  
        PushI        1                         
        Subtract                               
        PushD        $mem-array-float-length   
        Exchange                               
        StoreI                                 
        PushD        $mem-array-float-length   
        LoadI                                  
        JumpFalse    $mem-array-float-array    
        PushI        44                        
        PushD        $print-format-char        
        Printf                                 
        PushI        32                        
        PushD        $print-format-char        
        Printf                                 
        Jump         $mem-array-float-array    
        Label        $mem-array-float-print-end 
        PushI        93                        
        PushD        $print-format-char        
        Printf                                 
        PushD        $mem-array-float-return-address 
        LoadI                                  
        Return                                 
        Label        -mem-array-char-print     
        DLabel       $mem-array-char-return-address 
        DataZ        4                         
        DLabel       $mem-array-char-array-address 
        DataZ        4                         
        DLabel       $mem-array-char-end-array-address 
        DataZ        4                         
        DLabel       $mem-array-char-length    
        DataZ        4                         
        DLabel       $mem-array-char-size      
        DataZ        4                         
        PushD        $mem-array-char-return-address 
        Exchange                               
        StoreI                                 
        PushD        $mem-array-char-array-address 
        Exchange                               
        StoreI                                 
        PushD        $mem-array-char-array-address 
        LoadI                                  
        Call         -mem-arrya-record-valid   
        PushD        $mem-array-char-array-address 
        LoadI                                  
        PushI        4                         
        Add                                    
        LoadI                                  
        PushI        91                        
        PushD        $print-format-char        
        Printf                                 
        PushD        $mem-array-char-array-address 
        LoadI                                  
        PushI        12                        
        Add                                    
        LoadI                                  
        PushD        $mem-array-char-length    
        Exchange                               
        StoreI                                 
        PushD        $mem-array-char-array-address 
        LoadI                                  
        PushI        8                         
        Add                                    
        LoadI                                  
        PushD        $mem-array-char-size      
        Exchange                               
        StoreI                                 
        PushD        $mem-array-char-array-address 
        LoadI                                  
        PushI        16                        
        PushD        $mem-array-char-length    
        LoadI                                  
        PushD        $mem-array-char-size      
        LoadI                                  
        Multiply                               
        Add                                    
        Add                                    
        PushD        $mem-array-char-end-array-address 
        Exchange                               
        StoreI                                 
        JumpPos      $mem-array-char-array     
        Label        $mem-array-char-loop      
        PushD        $mem-array-char-length    
        LoadI                                  
        JumpFalse    $mem-array-char-print-end 
        PushD        $mem-array-char-end-array-address 
        LoadI                                  
        PushD        $mem-array-char-length    
        LoadI                                  
        PushD        $mem-array-char-size      
        LoadI                                  
        Multiply                               
        Subtract                               
        LoadC                                  
        PushD        $print-format-char        
        Printf                                 
        PushD        $mem-array-char-length    
        LoadI                                  
        PushI        1                         
        Subtract                               
        PushD        $mem-array-char-length    
        Exchange                               
        StoreI                                 
        PushD        $mem-array-char-length    
        LoadI                                  
        JumpFalse    $mem-array-char-loop      
        PushI        44                        
        PushD        $print-format-char        
        Printf                                 
        PushI        32                        
        PushD        $print-format-char        
        Printf                                 
        Jump         $mem-array-char-loop      
        Label        $mem-array-char-array     
        PushD        $mem-array-char-length    
        LoadI                                  
        JumpFalse    $mem-array-char-print-end 
        PushD        $mem-array-char-return-address 
        LoadI                                  
        PushD        $mem-array-char-array-address 
        LoadI                                  
        PushD        $mem-array-char-end-array-address 
        LoadI                                  
        PushD        $mem-array-char-length    
        LoadI                                  
        PushD        $mem-array-char-size      
        LoadI                                  
        PushD        $mem-array-char-end-array-address 
        LoadI                                  
        PushD        $mem-array-char-length    
        LoadI                                  
        PushD        $mem-array-char-size      
        LoadI                                  
        Multiply                               
        Subtract                               
        LoadI                                  
        Duplicate                              
        JumpFalse    $$a-record-error          
        Call         -mem-array-char-print     
        PushD        $mem-array-char-size      
        Exchange                               
        StoreI                                 
        PushD        $mem-array-char-length    
        Exchange                               
        StoreI                                 
        PushD        $mem-array-char-end-array-address 
        Exchange                               
        StoreI                                 
        PushD        $mem-array-char-array-address 
        Exchange                               
        StoreI                                 
        PushD        $mem-array-char-return-address 
        Exchange                               
        StoreI                                 
        PushD        $mem-array-char-length    
        LoadI                                  
        PushI        1                         
        Subtract                               
        PushD        $mem-array-char-length    
        Exchange                               
        StoreI                                 
        PushD        $mem-array-char-length    
        LoadI                                  
        JumpFalse    $mem-array-char-array     
        PushI        44                        
        PushD        $print-format-char        
        Printf                                 
        PushI        32                        
        PushD        $print-format-char        
        Printf                                 
        Jump         $mem-array-char-array     
        Label        $mem-array-char-print-end 
        PushI        93                        
        PushD        $print-format-char        
        Printf                                 
        PushD        $mem-array-char-return-address 
        LoadI                                  
        Return                                 
        Label        -mem-array-bool-print     
        DLabel       $mem-array-bool-return-address 
        DataZ        4                         
        DLabel       $mem-array-bool-array-address 
        DataZ        4                         
        DLabel       $mem-array-bool-end-array-address 
        DataZ        4                         
        DLabel       $mem-array-bool-length    
        DataZ        4                         
        DLabel       $mem-array-bool-size      
        DataZ        4                         
        PushD        $mem-array-bool-return-address 
        Exchange                               
        StoreI                                 
        PushD        $mem-array-bool-array-address 
        Exchange                               
        StoreI                                 
        PushD        $mem-array-bool-array-address 
        LoadI                                  
        Call         -mem-arrya-record-valid   
        PushD        $mem-array-bool-array-address 
        LoadI                                  
        PushI        4                         
        Add                                    
        LoadI                                  
        PushI        91                        
        PushD        $print-format-char        
        Printf                                 
        PushD        $mem-array-bool-array-address 
        LoadI                                  
        PushI        12                        
        Add                                    
        LoadI                                  
        PushD        $mem-array-bool-length    
        Exchange                               
        StoreI                                 
        PushD        $mem-array-bool-array-address 
        LoadI                                  
        PushI        8                         
        Add                                    
        LoadI                                  
        PushD        $mem-array-bool-size      
        Exchange                               
        StoreI                                 
        PushD        $mem-array-bool-array-address 
        LoadI                                  
        PushI        16                        
        PushD        $mem-array-bool-length    
        LoadI                                  
        PushD        $mem-array-bool-size      
        LoadI                                  
        Multiply                               
        Add                                    
        Add                                    
        PushD        $mem-array-bool-end-array-address 
        Exchange                               
        StoreI                                 
        JumpPos      $mem-array-bool-array     
        Label        $mem-array-bool-loop      
        PushD        $mem-array-bool-length    
        LoadI                                  
        JumpFalse    $mem-array-bool-print-end 
        PushD        $mem-array-bool-end-array-address 
        LoadI                                  
        PushD        $mem-array-bool-length    
        LoadI                                  
        PushD        $mem-array-bool-size      
        LoadI                                  
        Multiply                               
        Subtract                               
        LoadC                                  
        JumpTrue     $mem-array-bool-true      
        PushD        $boolean-false-string     
        Jump         $mem-array-bool-end       
        Label        $mem-array-bool-true      
        PushD        $boolean-true-string      
        Label        $mem-array-bool-end       
        PushD        $print-format-string      
        Printf                                 
        PushD        $mem-array-bool-length    
        LoadI                                  
        PushI        1                         
        Subtract                               
        PushD        $mem-array-bool-length    
        Exchange                               
        StoreI                                 
        PushD        $mem-array-bool-length    
        LoadI                                  
        JumpFalse    $mem-array-bool-loop      
        PushI        44                        
        PushD        $print-format-char        
        Printf                                 
        PushI        32                        
        PushD        $print-format-char        
        Printf                                 
        Jump         $mem-array-bool-loop      
        Label        $mem-array-bool-array     
        PushD        $mem-array-bool-length    
        LoadI                                  
        JumpFalse    $mem-array-bool-print-end 
        PushD        $mem-array-bool-return-address 
        LoadI                                  
        PushD        $mem-array-bool-array-address 
        LoadI                                  
        PushD        $mem-array-bool-end-array-address 
        LoadI                                  
        PushD        $mem-array-bool-length    
        LoadI                                  
        PushD        $mem-array-bool-size      
        LoadI                                  
        PushD        $mem-array-bool-end-array-address 
        LoadI                                  
        PushD        $mem-array-bool-length    
        LoadI                                  
        PushD        $mem-array-bool-size      
        LoadI                                  
        Multiply                               
        Subtract                               
        LoadI                                  
        Duplicate                              
        JumpFalse    $$a-record-error          
        Call         -mem-array-bool-print     
        PushD        $mem-array-bool-size      
        Exchange                               
        StoreI                                 
        PushD        $mem-array-bool-length    
        Exchange                               
        StoreI                                 
        PushD        $mem-array-bool-end-array-address 
        Exchange                               
        StoreI                                 
        PushD        $mem-array-bool-array-address 
        Exchange                               
        StoreI                                 
        PushD        $mem-array-bool-return-address 
        Exchange                               
        StoreI                                 
        PushD        $mem-array-bool-length    
        LoadI                                  
        PushI        1                         
        Subtract                               
        PushD        $mem-array-bool-length    
        Exchange                               
        StoreI                                 
        PushD        $mem-array-bool-length    
        LoadI                                  
        JumpFalse    $mem-array-bool-array     
        PushI        44                        
        PushD        $print-format-char        
        Printf                                 
        PushI        32                        
        PushD        $print-format-char        
        Printf                                 
        Jump         $mem-array-bool-array     
        Label        $mem-array-bool-print-end 
        PushI        93                        
        PushD        $print-format-char        
        Printf                                 
        PushD        $mem-array-bool-return-address 
        LoadI                                  
        Return                                 
        Label        -mem-array-string-print   
        DLabel       $mem-array-string-return-address 
        DataZ        4                         
        DLabel       $mem-array-string-array-address 
        DataZ        4                         
        DLabel       $mem-array-string-end-array-address 
        DataZ        4                         
        DLabel       $mem-array-string-length  
        DataZ        4                         
        DLabel       $mem-array-string-size    
        DataZ        4                         
        PushD        $mem-array-string-return-address 
        Exchange                               
        StoreI                                 
        PushD        $mem-array-string-array-address 
        Exchange                               
        StoreI                                 
        PushD        $mem-array-string-array-address 
        LoadI                                  
        Call         -mem-arrya-record-valid   
        PushD        $mem-array-string-array-address 
        LoadI                                  
        PushI        4                         
        Add                                    
        LoadI                                  
        PushI        91                        
        PushD        $print-format-char        
        Printf                                 
        PushD        $mem-array-string-array-address 
        LoadI                                  
        PushI        12                        
        Add                                    
        LoadI                                  
        PushD        $mem-array-string-length  
        Exchange                               
        StoreI                                 
        PushD        $mem-array-string-array-address 
        LoadI                                  
        PushI        8                         
        Add                                    
        LoadI                                  
        PushD        $mem-array-string-size    
        Exchange                               
        StoreI                                 
        PushD        $mem-array-string-array-address 
        LoadI                                  
        PushI        16                        
        PushD        $mem-array-string-length  
        LoadI                                  
        PushD        $mem-array-string-size    
        LoadI                                  
        Multiply                               
        Add                                    
        Add                                    
        PushD        $mem-array-string-end-array-address 
        Exchange                               
        StoreI                                 
        JumpPos      $mem-array-string-array   
        Label        $mem-array-string-loop    
        PushD        $mem-array-string-length  
        LoadI                                  
        JumpFalse    $mem-array-string-print-end 
        PushD        $mem-array-string-end-array-address 
        LoadI                                  
        PushD        $mem-array-string-length  
        LoadI                                  
        PushD        $mem-array-string-size    
        LoadI                                  
        Multiply                               
        Subtract                               
        LoadI                                  
        Duplicate                              
        JumpFalse    $$a-string-error          
        PushI        12                        
        Add                                    
        PushD        $print-format-string      
        Printf                                 
        PushD        $mem-array-string-length  
        LoadI                                  
        PushI        1                         
        Subtract                               
        PushD        $mem-array-string-length  
        Exchange                               
        StoreI                                 
        PushD        $mem-array-string-length  
        LoadI                                  
        JumpFalse    $mem-array-string-loop    
        PushI        44                        
        PushD        $print-format-char        
        Printf                                 
        PushI        32                        
        PushD        $print-format-char        
        Printf                                 
        Jump         $mem-array-string-loop    
        Label        $mem-array-string-array   
        PushD        $mem-array-string-length  
        LoadI                                  
        JumpFalse    $mem-array-string-print-end 
        PushD        $mem-array-string-return-address 
        LoadI                                  
        PushD        $mem-array-string-array-address 
        LoadI                                  
        PushD        $mem-array-string-end-array-address 
        LoadI                                  
        PushD        $mem-array-string-length  
        LoadI                                  
        PushD        $mem-array-string-size    
        LoadI                                  
        PushD        $mem-array-string-end-array-address 
        LoadI                                  
        PushD        $mem-array-string-length  
        LoadI                                  
        PushD        $mem-array-string-size    
        LoadI                                  
        Multiply                               
        Subtract                               
        LoadI                                  
        Duplicate                              
        JumpFalse    $$a-record-error          
        Call         -mem-array-string-print   
        PushD        $mem-array-string-size    
        Exchange                               
        StoreI                                 
        PushD        $mem-array-string-length  
        Exchange                               
        StoreI                                 
        PushD        $mem-array-string-end-array-address 
        Exchange                               
        StoreI                                 
        PushD        $mem-array-string-array-address 
        Exchange                               
        StoreI                                 
        PushD        $mem-array-string-return-address 
        Exchange                               
        StoreI                                 
        PushD        $mem-array-string-length  
        LoadI                                  
        PushI        1                         
        Subtract                               
        PushD        $mem-array-string-length  
        Exchange                               
        StoreI                                 
        PushD        $mem-array-string-length  
        LoadI                                  
        JumpFalse    $mem-array-string-array   
        PushI        44                        
        PushD        $print-format-char        
        Printf                                 
        PushI        32                        
        PushD        $print-format-char        
        Printf                                 
        Jump         $mem-array-string-array   
        Label        $mem-array-string-print-end 
        PushI        93                        
        PushD        $print-format-char        
        Printf                                 
        PushD        $mem-array-string-return-address 
        LoadI                                  
        Return                                 
        Label        -mem-array-rational-print 
        DLabel       $mem-array-rational-return-address 
        DataZ        4                         
        DLabel       $mem-array-rational-array-address 
        DataZ        4                         
        DLabel       $mem-array-rational-end-array-address 
        DataZ        4                         
        DLabel       $mem-array-rational-length 
        DataZ        4                         
        DLabel       $mem-array-rational-size  
        DataZ        4                         
        PushD        $mem-array-rational-return-address 
        Exchange                               
        StoreI                                 
        PushD        $mem-array-rational-array-address 
        Exchange                               
        StoreI                                 
        PushD        $mem-array-rational-array-address 
        LoadI                                  
        Call         -mem-arrya-record-valid   
        PushD        $mem-array-rational-array-address 
        LoadI                                  
        PushI        4                         
        Add                                    
        LoadI                                  
        PushI        91                        
        PushD        $print-format-char        
        Printf                                 
        PushD        $mem-array-rational-array-address 
        LoadI                                  
        PushI        12                        
        Add                                    
        LoadI                                  
        PushD        $mem-array-rational-length 
        Exchange                               
        StoreI                                 
        PushD        $mem-array-rational-array-address 
        LoadI                                  
        PushI        8                         
        Add                                    
        LoadI                                  
        PushD        $mem-array-rational-size  
        Exchange                               
        StoreI                                 
        PushD        $mem-array-rational-array-address 
        LoadI                                  
        PushI        16                        
        PushD        $mem-array-rational-length 
        LoadI                                  
        PushD        $mem-array-rational-size  
        LoadI                                  
        Multiply                               
        Add                                    
        Add                                    
        PushD        $mem-array-rational-end-array-address 
        Exchange                               
        StoreI                                 
        JumpPos      $mem-array-rational-array 
        Label        $mem-array-rational-loop  
        PushD        $mem-array-rational-length 
        LoadI                                  
        JumpFalse    $mem-array-rational-print-end 
        PushD        $mem-array-rational-end-array-address 
        LoadI                                  
        PushD        $mem-array-rational-length 
        LoadI                                  
        PushD        $mem-array-rational-size  
        LoadI                                  
        Multiply                               
        Subtract                               
        Duplicate                              
        LoadI                                  
        Exchange                               
        PushI        4                         
        Add                                    
        LoadI                                  
        Duplicate                              
        JumpFalse    $$r-denominator-zero      
        Call         -mem-rat-print            
        PushD        $mem-array-rational-length 
        LoadI                                  
        PushI        1                         
        Subtract                               
        PushD        $mem-array-rational-length 
        Exchange                               
        StoreI                                 
        PushD        $mem-array-rational-length 
        LoadI                                  
        JumpFalse    $mem-array-rational-loop  
        PushI        44                        
        PushD        $print-format-char        
        Printf                                 
        PushI        32                        
        PushD        $print-format-char        
        Printf                                 
        Jump         $mem-array-rational-loop  
        Label        $mem-array-rational-array 
        PushD        $mem-array-rational-length 
        LoadI                                  
        JumpFalse    $mem-array-rational-print-end 
        PushD        $mem-array-rational-return-address 
        LoadI                                  
        PushD        $mem-array-rational-array-address 
        LoadI                                  
        PushD        $mem-array-rational-end-array-address 
        LoadI                                  
        PushD        $mem-array-rational-length 
        LoadI                                  
        PushD        $mem-array-rational-size  
        LoadI                                  
        PushD        $mem-array-rational-end-array-address 
        LoadI                                  
        PushD        $mem-array-rational-length 
        LoadI                                  
        PushD        $mem-array-rational-size  
        LoadI                                  
        Multiply                               
        Subtract                               
        LoadI                                  
        Duplicate                              
        JumpFalse    $$a-record-error          
        Call         -mem-array-rational-print 
        PushD        $mem-array-rational-size  
        Exchange                               
        StoreI                                 
        PushD        $mem-array-rational-length 
        Exchange                               
        StoreI                                 
        PushD        $mem-array-rational-end-array-address 
        Exchange                               
        StoreI                                 
        PushD        $mem-array-rational-array-address 
        Exchange                               
        StoreI                                 
        PushD        $mem-array-rational-return-address 
        Exchange                               
        StoreI                                 
        PushD        $mem-array-rational-length 
        LoadI                                  
        PushI        1                         
        Subtract                               
        PushD        $mem-array-rational-length 
        Exchange                               
        StoreI                                 
        PushD        $mem-array-rational-length 
        LoadI                                  
        JumpFalse    $mem-array-rational-array 
        PushI        44                        
        PushD        $print-format-char        
        Printf                                 
        PushI        32                        
        PushD        $print-format-char        
        Printf                                 
        Jump         $mem-array-rational-array 
        Label        $mem-array-rational-print-end 
        PushI        93                        
        PushD        $print-format-char        
        Printf                                 
        PushD        $mem-array-rational-return-address 
        LoadI                                  
        Return                                 
        Label        -mem-arrya-record-valid   
        DLabel       -mem-arrya-record-valid-return-address 
        DataZ        4                         
        DLabel       -mem-arrya-record-valid-array-address 
        DataZ        4                         
        PushD        -mem-arrya-record-valid-return-address 
        Exchange                               
        StoreI                                 
        PushD        -mem-arrya-record-valid-array-address 
        Exchange                               
        StoreI                                 
        PushD        -mem-arrya-record-valid-array-address 
        LoadI                                  
        LoadI                                  
        PushI        7                         
        Subtract                               
        JumpTrue     $$a-record-error          
        PushD        -mem-arrya-record-valid-array-address 
        LoadI                                  
        PushI        4                         
        Add                                    
        LoadI                                  
        PushI        2                         
        BTAnd                                  
        JumpTrue     $$a-record-deleted-error  
        PushD        -mem-arrya-record-valid-return-address 
        LoadI                                  
        Return                                 
        DLabel       $heap-memory              
