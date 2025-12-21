package com.rootbly.openpulse.common.constants

/**
 * GitHub Topics category definitions
 * 17 major categories based on actual data analysis
 */
object TopicCategories {

    /**
     * Keyword mapping by category
     * Priority: top to bottom (first matching category is selected)
     */
    val CATEGORY_KEYWORDS = mapOf(
        // 1. AI & Machine Learning (most specific AI-related)
        "AI & Machine Learning" to listOf(
            // LLM
            "llm", "gpt", "gpt-4o", "chatgpt", "claude", "claude-ai", "claude-code", "claude-desktop",
            "gemini", "gemini-ai", "gemini-api", "anthropic", "openai", "openai-api",
            "ollama", "llama", "llama3", "deepseek", "mistral", "qwen", "qwen3",
            // AI General
            "ai", "artificial-intelligence", "machine-learning", "ml",
            "deep-learning", "neural-network", "neural-networks", "deep-neural-networks",
            "convolutional-neural-networks", "cnn", "lstm",
            // Frameworks
            "pytorch", "tensorflow", "keras", "transformers", "huggingface",
            // Technologies
            "nlp", "natural-language-processing", "computer-vision", "cv",
            "reinforcement-learning", "embeddings", "bert", "yolo",
            "sentiment-analysis", "text-classification", "image-classification",
            "object-detection", "ocr", "speech-recognition", "speech-to-text",
            "text-to-speech", "tts", "whisper", "stable-diffusion", "diffusion",
            "diffusion-models", "image-generation", "generative-ai", "genai", "gen-ai",
            "anomaly-detection", "face-recognition", "pose-estimation",
            "supervised-learning", "unsupervised-learning", "reinforcement-learning",
            "transfer-learning", "data-augmentation", "feature-extraction",
            "image-segmentation", "semantic-segmentation", "word2vec",
            "named-entity-recognition", "sentiment-classification",
            "voice-recognition", "voice-conversion", "voice-cloning",
            "text-to-image", "text-to-image-generation", "text-to-video",
            "gan", "generative-adversarial-network", "generative-model",
            "attention-mechanism", "transformer-models",
            // Medical AI
            "medical-ai", "medical-image-processing", "disease-prediction",
            // Model
            "model-training", "fine-tuning", "onnx", "mlx", "mediapipe", "mlflow"
        ),

        // 2. AI Tools & Integration (AI utilization tools)
        "AI Tools & Integration" to listOf(
            // Agent
            "ai-agent", "ai-agents", "agent", "agents", "agentic", "agentic-ai",
            "agentic-workflow", "autonomous-agents", "multi-agent", "multi-agent-system",
            "aiagent", "aiagents", "llm-agent", "llm-agents",
            "sub-agents", "subagents", "agent-framework", "ai-agents-framework",
            "agentic-framework", "agentic-ai-development", "agent-based-modeling",
            // RAG & Tools
            "rag", "retrieval-augmented-generation", "langchain", "langgraph",
            "langchain-python", "langchain4j",
            "mcp", "mcp-server", "mcp-client", "mcp-tools", "model-context-protocol",
            "mcp-servers", "mcp-gateway", "mcp-host", "mcp-clients",
            "model-context-protocol-servers", "model-context-protocol-server",
            "openrouter", "mistral",
            // AI Tools
            "ai-tools", "ai-assistant", "ai-chatbot", "chatbot", "chatbots",
            "prompt-engineering", "prompt", "prompts", "conversational-ai",
            "ai-integration", "ai-powered", "ai-memory",
            "context-engineering", "system-prompts", "prompting",
            // Evaluation/Operations
            "llm-evaluation", "llm-inference", "llmops", "mlops", "aiops",
            "ai-integration", "semantic-search", "vector-search", "vector-database",
            "vector", "chromadb", "pinecone", "faiss", "qdrant", "milvus",
            "language-models", "local-llm",
            // AI Safety & Ethics
            "ai-safety", "ai-ethics", "ai-consciousness", "ai-philosophy",
            // Specific Tools
            "cursor-ai", "cursor-ide", "cursors", "cursor-ai-editor",
            "windsurf-ai", "cursorai", "cline",
            "groq", "groq-api", "perplexity",
            "claude-skills", "claude-code-plugin", "claude-code-subagents", "claude-code-hooks",
            "gemini-cli", "google-gemini",
            // Frameworks
            "llm-framework", "llm-gateway", "ai-gateway", "ai-framework",
            "llm-observability", "guardrails"
        ),

        // 3. Programming Languages
        "Programming Languages" to listOf(
            "python", "python3", "python-library", "python-script", "python-3",
            "python-lang", "python-language",
            "javascript", "js", "javascipt", "typescript", "ts", "nodejs", "node", "node-js",
            "java", "kotlin", "kotlin-android", "kotlin-multiplatform", "kotlin-js", "kotlin-flow",
            "rust", "rust-lang", "rust-library", "go", "golang", "pure-go",
            "cpp", "c-plus-plus", "c", "cpp20", "cpp23", "modern-cpp", "cpp-library",
            "csharp", "c-sharp", "csharp-library",
            "php", "php-fpm", "php-framework", "ruby", "rails", "swift", "dart",
            "lua", "luajit", "perl", "perl5", "scala", "groovy", "groovy-script",
            "haskell", "elixir", "erlang", "julia", "ocaml", "common-lisp",
            "clojure", "clojurescript", "scheme",
            "objective-c", "r", "rstats", "r-packages", "cran",
            "assembly", "x64", "aarch64", "riscv",
            "matlab", "octave", "fortran",
            "verilog", "vhdl", "systemverilog",
            "bash", "shell", "zsh", "fish", "linux-shell",
            "dotnet", ".net", "netcore", "asp-net", "asp-net-core", "aspnetcore",
            "zig", "no-std",
            "programming", "programming-language", "programming-languages"
        ),

        // 4. Web Development
        "Web Development" to listOf(
            // React Ecosystem
            "react", "reactjs", "react-native", "react-hooks", "react-router",
            "react-router-dom", "react-redux", "react-query", "react-bootstrap",
            "react-components", "react-component", "react-three-fiber",
            "react-template", "react19", "create-react-app",
            "nextjs", "nextjs14", "nextjs15", "nextjs16", "nextjs-template", "nextjs-typescript",
            "remix",
            // Vue Ecosystem
            "vue", "vuejs", "vue3", "vue3-composition-api", "nuxt", "nuxt3", "sveltekit", "svelte5",
            // Other Frameworks
            "angular", "svelte", "astro", "astrojs", "solidjs",
            // Basics
            "html", "html5", "html-css", "html-css-javascript",
            "css", "css3", "css-lang", "css-language", "css-modules",
            // CSS Frameworks
            "tailwind", "tailwindcss", "tailwind-css", "tailwindcss-v4", "bootstrap", "bootstrap5", "sass", "scss",
            "material-design", "material-ui", "material3", "shadcn-ui", "shadcn", "daisyui",
            "styled-components", "ant-design", "antd", "chakra-ui", "heroui",
            "css-framework",
            // Tools
            "vite", "webpack", "rollup", "bun", "pnpm", "npm", "npm-package",
            "turborepo", "yarn",
            // General
            "web", "web-development", "web-app", "webapp", "website",
            "frontend", "frontend-development", "backend", "backend-development",
            "fullstack", "full-stack", "ssr", "pwa", "spa", "single-page-app",
            "responsive", "responsive-design", "ui", "ui-components", "component-library",
            "ui-library", "ui-kit", "headless", "headless-cms",
            "web-components", "webcomponents", "web-dashboard",
            // UI/UX
            "ui-design", "dark-mode", "dark-theme",
            // Frameworks
            "express", "expressjs", "fastapi", "fastify", "flask", "flask-api", "flask-application",
            "django", "django-rest-framework", "django-admin", "laravel", "laravel-package", "laravel-nova",
            "spring-boot", "springboot", "java-spring-boot", "springboot2",
            "spring", "spring-cloud", "spring-security", "spring-mvc", "spring-ai",
            "nestjs", "phoenix", "symfony", "asp-net-core", "aspnetcore",
            "hono", "gin", "fiber", "echo",
            // Others
            "htmx", "alpinejs", "lucide-react", "recharts", "tanstack-query",
            "context-api", "zustand", "swr", "react-toastify",
            "jquery", "jquery-plugin", "ajax", "fetch", "fetch-api", "axios",
            "websocket", "socket-io", "server-sent-events",
            "web-assembly", "wasm", "webgl", "webgl2",
            "progressive-web-app", "pwa", "offline-support", "offline-first",
            "hot-reload", "hmr",
            "content-management", "cms", "cms-framework", "content-management-system",
            "webui", "web-tools", "web-development-tools", "webdev",
            "single-page-app", "spa", "server-side-rendering", "ssr",
            "layout", "responsive-ui", "mobile-first",
            "animation-css", "svg-animations",
            "web-standards", "whatwg", "wcag",
            "website-template", "website-builder", "web-dashboard",
            "feature-flags", "feature-toggles",
            "cdn", "cors", "session", "custom-session",
            "routing", "router",
            "api-documentation", "swagger-ui", "openapi", "openapi31",
            "webscraping", "web-scraper", "web-automation", "scrapy",
            "lighthouse", "performance-tuning",
            "blade", "ejs", "pug", "handlebars", "mustache", "templating",
            "hugo", "jekyll", "gatsby", "11ty", "pelican",
            "stenciljs", "polymer", "lit", "web-components",
            "meteor", "sails", "adonis", "feathers", "loopback",
            "codeigniter", "cakephp", "yii", "slim", "lumen",
            "play-framework", "vertx", "micronaut", "quarkus",
            "rails", "sinatra", "padrino", "hanami",
            "asp-net", "blazor-server", "blazor-webassembly", "razor", "winui",
            "xamarin", "maui", "xaml",
            "vaadin", "gwt", "wicket", "tapestry",
            "odoo", "magento", "magento2-extension"
        ),

        // 5. Mobile Development
        "Mobile Development" to listOf(
            "mobile", "mobile-app", "mobile-apps", "mobile-development", "mobile-game", "mobile-gaming",
            "android", "android-app", "android-studio", "android-automation",
            "android-ui", "android-library", "android-security",
            "ios", "swift", "swiftui", "xcode", "macos", "macos-app", "apple", "apple-silicon",
            "watchos", "tvos", "visionos",
            "flutter", "flutter-apps", "flutter-package", "flutter-plugin", "flutter-web",
            "react-native",
            "kotlin-android", "kotlin-multiplatform", "compose-multiplatform",
            "jetpack-compose", "jetpack", "livedata", "databinding",
            "expo", "cordova",
            "appium", "appilot", "ui-automator",
            "xamarin", "maui",
            "ionic", "capacitor", "nativescript",
            "iphone", "ipad", "playstore", "appstore",
            "ios-app", "mobile-first",
            "file-transfer-android", "adb-less-control",
            "haptic-feedback", "biometrics",
            "push-notifications", "fcm", "apns"
        ),

        // 6. DevOps & Infrastructure
        "DevOps & Infrastructure" to listOf(
            // Containers
            "docker", "docker-compose", "docker-image", "docker-container", "docker-swarm",
            "dockerfile", "dockerhub", "docker-registry",
            "kubernetes", "k8s", "k8s-at-home", "k3s", "helm", "helm-chart", "helm-charts",
            "kubernetes-operator", "kubernetes-controller", "kubernetes-cluster",
            "kustomize", "rancher", "portainer",
            "containers", "container", "containerization",
            // CI/CD
            "devops", "cicd", "ci-cd", "ci",
            "github-actions", "github-action", "actions", "gitlab", "jenkins", "jenkins-cft",
            "continuous-integration", "continuous-delivery", "continuous-deployment",
            // GitOps
            "gitops", "argocd", "flux", "fluxcd", "renovate",
            // IaC
            "terraform", "terraform-provider", "ansible",
            "infrastructure-as-code", "infrastructure",
            // Monitoring
            "monitoring", "observability", "prometheus", "grafana", "grafana-dashboard",
            "metrics", "logging", "telemetry", "tracing", "distributed-tracing",
            "opentelemetry", "apm", "datadog", "siem", "loki", "victoriametrics",
            "sentry", "crash-reporting", "error-monitoring",
            "splunk", "elk", "logstash", "kibana",
            "monitoring-tools", "monitoring-tool", "network-monitoring",
            // Cloud
            "cloud", "cloud-computing", "cloud-native", "serverless", "cloud-deployment",
            "cloud-security", "edge-computing", "multi-cloud",
            "aws", "aws-lambda", "aws-s3", "aws-ec2", "aws-sdk", "aws-dynamodb", "aws-bedrock",
            "amazon-web-services", "amazon-s3", "cloudformation", "cloudtrail",
            "azure", "azure-openai", "azure-devops", "azure-sdk",
            "gcp", "google-cloud", "google-adk",
            "heroku", "vercel", "netlify", "cloudflare", "cloudflare-pages",
            "digitalocean", "linode", "scaleway",
            "paas", "iaas", "saas",
            "openshift", "nomad", "consul", "vault", "hashicorp-vault", "hashicorp",
            "render-deployment",
            // Orchestration
            "orchestration", "workflow", "workflows", "workflow-automation",
            "workflow-engine", "airflow", "apache-airflow", "n8n", "n8n-workflow",
            "temporal", "cadence", "dagster", "prefect", "luigi",
            "kestra", "argo", "argo-workflows",
            // Service Mesh
            "service-mesh", "istio", "linkerd", "consul", "envoy",
            "api-gateway", "gateway-api", "ingress-controller", "kong",
            // Automation
            "automation", "devops-automation", "devops-tools",
            "ansible-role", "ansible-playbook", "playbook",
            "terraform-module", "terraform-provider",
            "pulumi", "crossplane",
            // Scaling
            "autoscaling", "load-balancer", "load-balancing", "high-availability",
            "fault-tolerance", "resiliency", "disaster-recovery",
            // Networking
            "networking", "network", "vpn", "vpn-manager", "dns", "dhcp",
            "proxy", "reverse-proxy", "nginx", "caddy", "traefik", "haproxy",
            "iptables", "firewall", "fail2ban", "security-groups",
            "vlan", "inter-vlan-routing", "ivr", "network-simulation",
            "network-analysis", "network-scanner",
            // Virtualization
            "virtualization", "vm", "virtual-machine", "kvm", "qemu",
            "vmware", "virtualbox", "hypervisor", "esxi",
            "proxmox", "proxmox-ve", "lxc", "lxd",
            // Backup/Recovery
            "backup", "restore", "recovery", "disaster-recovery",
            "snapshot", "replication", "data-replication", "sync",
            // Secrets Management
            "secrets", "secrets-management", "secret", "vault",
            "environment-variables", "env", "dotenv", "config",
            "key-management", "certificate", "tls-certificate", "ssl", "pki",
            "acme", "letsencrypt", "x509",
            // Platform
            "platform-engineering", "internal-tools", "backstage",
            // Linux/Unix
            "linux", "linux-kernel", "ubuntu", "debian", "centos", "rhel", "fedora",
            "arch", "arch-linux", "aur",
            "alpine", "alpine-linux", "busybox",
            "unix", "bsd", "freebsd", "openbsd", "osx", "macos",
            "openwrt", "talos-linux",
            // Others
            "deployment", "deploy", "configuration-management", "configuration",
            "baremetal", "bare-metal", "edge", "iot", "iiot",
            "homelab", "homelab-setup", "home-ops", "self-hosting",
            "sre", "reliability-engineering", "chaos-engineering",
            "gitops", "argocd", "flux", "flux2", "fluxcd", "renovate",
            "registry", "harbor", "nexus", "artifactory",
            "bastion", "jumphost", "ssh", "sftp", "telnet",
            "systemd", "init", "service", "daemon",
            "cron", "crontab", "task-scheduling", "scheduler",
            "process-management", "supervisor", "pm2",
            "package-management", "package-manager",
            "compliance", "audit", "auditing", "governance", "iso27001",
            "sbom", "attestation", "provenance"
        ),

        // 7. Database & Data Storage
        "Database & Data Storage" to listOf(
            // SQL
            "database", "database-management", "sql", "sql-database",
            "postgresql", "postgres", "postgresql-database", "mysql", "mariadb",
            "sqlite", "sqlite3", "sql-server", "mssql", "oracle",
            "cassandra", "rethinkdb", "tidb",
            // NoSQL
            "mongodb", "mongodb-atlas", "redis", "elasticsearch", "opensearch",
            "dynamodb", "clickhouse", "influxdb", "nosql",
            // ORM/Query
            "prisma", "drizzle-orm", "drizzle", "orm", "sqlalchemy", "mongoose",
            "query-optimization", "gorm", "jpa", "spring-data-jpa",
            // Vector/Embedding
            "vector-database", "chromadb", "pinecone", "faiss", "qdrant", "milvus",
            "graph-database", "neo4j",
            // Data Warehouse
            "data-warehouse", "snowflake", "bigquery", "redshift",
            "clickhouse", "lakehouse", "iceberg", "databricks",
            // Others
            "s3", "aws-s3", "s3-bucket", "cloud-storage", "storage", "data-storage",
            "cache", "caching", "firestore", "firebase-realtime-database",
            "benchmark", "amd", "duckdb"
        ),

        // 8. Data Science & Analytics
        "Data Science & Analytics" to listOf(
            // Basics
            "data-science", "data-analysis", "data-analytics", "analytics",
            "data-visualization", "visualization",
            // Libraries
            "pandas", "pandas-dataframe", "pandas-python", "numpy", "ndarray",
            "matplotlib", "matplotlib-pyplot", "seaborn", "plotly",
            "scikit-learn", "scipy", "opencv", "opencv-python",
            // Tools
            "jupyter-notebook", "jupyter", "jupyterlab", "notebook",
            "streamlit", "streamlit-webapp", "streamlit-dashboard", "streamlit-application",
            "gradio", "tableau", "powerbi", "powerbi-report",
            // Processing
            "data-processing", "data-engineering", "data-pipelines", "data-pipeline",
            "etl", "etl-pipeline", "elt", "pipeline", "pipelines",
            "data-cleaning", "data-mining", "data-quality", "data-validation",
            "data-transformation", "data-augmentation",
            // Statistics/Analysis
            "statistics", "exploratory-data-analysis", "eda",
            "feature-engineering", "feature-extraction",
            "regression", "logistic-regression", "linear-regression",
            "random-forest", "xgboost", "lightgbm", "catboost",
            "classification", "clustering", "kmeans", "knn",
            "predictive-modeling", "forecasting", "prediction",
            "time-series", "time-series-analysis", "time-series-database", "tsdb",
            "recommender-system", "recommendation-system", "collaborative-filtering",
            "correlation-analysis", "pca", "pca-analysis",
            "decision-trees", "gradient-boosting", "ensemble-learning",
            // Big Data
            "big-data", "bigdata", "bigquery", "redshift", "snowflake", "spark", "pyspark",
            "kafka", "apache-kafka", "kafka-producer", "kafka-consumer",
            "dbt", "duckdb", "dataops", "airflow", "apache-airflow",
            "hadoop", "hdfs", "flink",
            // Datasets
            "dataset", "datasets", "dataframe", "ndarray", "arrow", "parquet",
            "tabular-data"
        ),

        // 9. Blockchain & Crypto
        "Blockchain & Crypto" to listOf(
            // Blockchain
            "blockchain", "blockchain-technology", "blockchain-game",
            "web3", "dapp", "defi",
            // Chains
            "ethereum", "eth", "ethereum-dapp", "ethereum-contract", "ethereum-api",
            "bitcoin", "btc", "bitcoin-cash", "solana", "bnb", "bsc", "bnb-chain",
            "litecoin", "evm", "optimism", "arbitrum", "polygon", "base",
            "cardano", "polkadot", "cosmos", "ripple", "tron", "ton",
            // Technologies
            "smart-contracts", "smart-contract", "smartcontract", "solidity",
            "hardhat", "truffle", "foundry", "anchor", "ganache",
            "nft", "token", "crypto-token", "erc20",
            "amm", "liquidity", "staking", "minting",
            // DeFi
            "defi", "dex", "flash-loan", "flashloan", "swap",
            "uniswap", "raydium", "meteora", "polymarket",
            "chainlink", "oracle",
            // Tools/Wallets
            "wallet", "crypto-wallet", "cold-wallet", "hardware-wallet", "wallet-security",
            "metamask", "metamask-wallet", "metamask-extension", "metamask-plugin",
            "metamask-snap", "metamask-bot", "metamask-desktop",
            "phantom-wallet", "phantom-wallet-api", "phantom-wallet-bot",
            "walletconnect", "exodus", "exodus-wallet", "exodus-api",
            "exodus-app", "exodus-payment",
            // Trading/Finance
            "crypto", "cryptocurrency", "cryptocurrencies", "cryptocurrency-trading",
            "cryptocurrency-exchanges", "cash",
            "trading", "trading-bot", "crypto-trading", "crypto-bot",
            "arbitrage", "mev", "dex", "flash-loan",
            "trading-strategies", "trading-algorithms", "algorithmic-trading",
            "investment-strategies", "quantitative-trading",
            "binance", "binance-api", "protocol",
            "sniper-bot", "mevbots",
            // Games
            "blockchain-game", "crypto-game", "cryptocurrency-game", "ethereum-game",
            "play-to-earn",
            // Others
            "web3", "web3js", "dapp", "dapps", "etherscan", "ethersjs",
            "multisig", "proof-of-work", "consensus", "decentralization",
            "decentralized", "decentralized-ai",
            "memecoin", "coingecko",
            // Games
            "blockchain-game", "crypto-game", "cryptocurrency-game", "ethereum-game",
            "play-to-earn"
        ),

        // 10. Security & Hacking
        "Security & Hacking" to listOf(
            // Security General
            "security", "cybersecurity", "cyber-security", "infosec",
            "security-tools", "security-tool", "security-research",
            "devsecops", "network-security", "cloud-security", "windows-security",
            // Authentication/Encryption
            "authentication", "authorization", "auth", "oauth", "oauth2",
            "2fa", "jwt", "oidc", "sso",
            "encryption", "cryptography", "aes", "sha256", "hash",
            "access-control", "active-directory", "iam", "identity",
            "user-authentication", "user-management", "permissions",
            "rbac",
            // Attack/Penetration
            "hacking", "hack", "ethical-hacking", "pentesting", "penetration-testing",
            "pentest", "pentest-tool", "pentesting-tools",
            "exploit", "exploit-development", "vulnerability",
            "vulnerability-assessment", "vulnerability-management", "vulnerability-detection",
            "vulnerability-scanner", "vulnerability-scanning", "cve", "cve-scanning",
            "malware", "malware-analysis", "malware-detection", "malware-development",
            "malware-research", "ransomware", "backdoor", "trojan",
            "reverse-engineering", "obfuscation",
            "dll-injection", "dll-hijacking", "dll-hooking", "injection",
            "privilege-escalation", "evasion", "evasion-techniques",
            "av-evasion", "edr", "edr-bypass",
            "shellcode", "shellcode-injection", "payload", "payload-encryption",
            "in-memory-execution", "thread-injection",
            // Windows Security
            "windows-api", "windows-internals", "windows-malware", "windows-security",
            "windows-exploitation",
            // Tools/Techniques
            "osint", "osint-tool", "osint-resources", "reconnaissance", "recon",
            "red-team", "redteam", "red-teaming", "blue-team", "blueteam",
            "offensive-security", "threat-intelligence", "threatintel", "threat-hunting",
            "threat-detection", "incident-response", "forensics", "dfir", "digital-forensics",
            "fuzzing", "phishing", "phishing-detection",
            "proxy", "vpn", "anonymity", "anonymous", "privacy", "privacy-tools", "privacy-protection",
            "tor", "censorship", "siem",
            // Specific Tools
            "metasploit", "cobalt-strike", "nmap", "hydra", "suricata", "zeek", "frida",
            // MITRE
            "mitre-attack",
            // Network Security
            "network-scanner", "network-monitoring", "network-analysis"
        ),

        // 11. Proxy & Network Tools
        "Proxy & Network Tools" to listOf(
            // VPN/Proxy
            "vpn", "proxy", "proxies", "proxy-server", "proxy-list",
            "proxy-scraper", "proxy-checker", "free-proxy", "free-proxy-list",
            "http-proxy", "https-proxy", "socks", "socks4", "socks4-proxy",
            "socks5", "socks5-proxy", "socks-proxy",
            // Chinese Tools
            "shadowsocks", "v2ray", "v2rayn", "v2rayng", "vmess", "vless",
            "clash", "wireguard", "openvpn", "trojan",
            // Networking
            "networking", "network", "network-security", "dns", "dns-server",
            "firewall", "gateway", "reverse-proxy", "traefik", "nginx",
            // Bypass
            "gfw", "censorship", "china", "chinese-communist-party", "totalitarian",
            "falun-gong", "iran", "human-rights"
        ),

        // 12. Bot & Automation
        "Bot & Automation" to listOf(
            "bot", "bots", "automation", "automation-tools", "automation-scripts",
            // Telegram
            "telegram", "telegram-bot", "telegrambot", "telegram-bots",
            "telegram-proxy", "telegram-store", "telegram-store-bot",
            "telethon", "aiogram",
            // Discord
            "discord", "discord-bot", "discordbot", "discord-js", "discord-py",
            "discord-api", "discord-bots", "discord-bot-framework",
            "discord-bot-template", "discord-bot-dashboard",
            // Other Platforms
            "chatbot", "chatbots", "ai-chatbot", "chatbot-development",
            "conversational-ai", "conversational-agents",
            "whatsapp", "whatsapp-api", "facebook", "twitter", "twitter-bot",
            "instagram", "instagram-api", "tiktok",
            "slack-bot", "slackbot",
            // Automation
            "auto", "auto-clicker", "auto-farm", "game-bot", "game-cheat",
            "workflow-automation", "workflow-engine", "n8n", "n8n-workflow",
            "rss", "rss-feed", "rss-aggregator", "rss-reader", "rss-generator",
            // Social Media Automation
            "youtube-bot", "youtube-automation",
            "quora-automation"
        ),

        // 13. API & Integration
        "API & Integration" to listOf(
            "api", "api-development", "api-integration", "api-rest", "backend-api",
            "rest", "rest-api", "restful-api", "rest-client",
            "graphql", "grpc", "rpc", "trpc", "jsonrpc",
            "microservices", "microservice", "microservices-architecture",
            "websocket", "socket-io", "socket", "sockets", "webrtc", "realtime-messaging",
            "openapi", "openapi3", "openapi31", "openapiclient", "swagger", "swagger-ui",
            "api-documentation", "api-testing", "api-gateway", "api-security",
            "sdk", "api-client", "httpclient", "http-client", "fetch", "fetch-api",
            "axios", "requests", "aiohttp", "curl",
            "protobuf", "grpc-web", "msgpack", "avro", "thrift",
            "github-api", "gitlab-api", "bitbucket-api",
            "oauth", "oauth2", "oidc", "openid-connect", "openid", "jwt", "jwt-authentication",
            "authentication-backend", "authentication-service", "auth", "auth0",
            "clerk", "fusionauth", "keycloak", "authelia",
            "webhook", "webhooks", "server-sent-events", "sse", "eventsource",
            "message-broker", "rabbitmq", "kafka", "redis-streams", "pulsar", "nats",
            "amqp", "mqtt", "stomp", "zeromq",
            "servicebus", "eventbridge", "sns", "sqs",
            "bff", "backend-for-frontend",
            "enterprise-integration", "integration-testing",
            "mq", "message-queue", "pub-sub", "pubsub",
            "coap", "xmpp",
            "json-api", "json-parser", "json", "xml", "yaml", "toml",
            "postman", "insomnia", "httpie", "apidog"
        ),

        // 14. Game Development
        "Game Development" to listOf(
            "game", "games", "gaming", "gamedev", "game-development", "game-dev",
            "game-design", "game-engine", "engine",
            // Engines
            "unity", "unity3d", "godot", "godot4", "bevy", "raylib",
            // Genres
            "3d", "2d", "2d-game", "2d-games", "3d-game", "multiplayer", "multiplayer-game",
            "multiplayer-game-server", "mmorpg", "platformer", "rpg", "rpg-game",
            "adventure-game", "browser-game",
            // Specific Games
            "minecraft", "minecraft-server", "minecraft-mod", "minecraft-plugin",
            "minecraft-launcher", "minecraft-bedrock-edition",
            "roblox", "pokemon", "genshin-impact",
            "fortnite", "valorant", "cs2", "csgo", "csgo2", "counter-strike", "counter-strike-2",
            "call-of-duty", "lol", "among-us", "axie-infinity", "axie-infinity-game",
            // Game Bots/Cheats
            "game-bot", "game-cheat", "fivem", "fivem-cheat", "fivem-hack",
            "fivem-aimbot", "fivem-esp", "fivem-script", "fivem-spoofer",
            "valorant-cheat", "valorant-hack", "valorant-aimbot", "valorant-esp",
            "qbcore",
            // Others
            "mini-game", "clicker-game", "simulation", "simulator",
            "physics", "physics-simulation", "physics-engine",
            "graphics", "webgl", "webgl2", "opengl", "vulkan", "shaders",
            "threejs", "canvas",
            // Game Servers
            "game-server", "spigot-plugin", "bungeecord", "bukkit",
            // Game Libraries
            "pygame", "game-library",
            // Game Tools
            "steam", "steam-games"
        ),

        // 15. Testing & Quality
        "Testing & Quality" to listOf(
            "testing", "test", "test-automation", "testing-tools", "testing-library",
            // E2E Testing
            "selenium", "selenium-python", "playwright", "puppeteer", "cypress",
            "e2e", "e2e-testing", "end-to-end-testing",
            // Unit Testing
            "jest", "vitest", "pytest", "junit", "junit5", "mocha", "phpunit",
            "unit-test", "unit-testing", "mockito", "mock",
            // API Testing
            "api-testing", "rest-client", "postman", "swagger-ui",
            // Code Quality
            "tdd", "bdd", "testing-tools",
            "code-quality", "code-analysis", "static-analysis", "static-code-analysis",
            "linter", "eslint", "eslint-config", "prettier", "ruff",
            "code-review", "verification", "validation",
            "benchmark", "profiling", "performance-analysis", "performance-testing",
            "performance-monitoring", "performance-metrics",
            "batch", "evaluation", "qa", "qa-automation",
            // Coverage
            "coverage", "code-coverage",
            // Others
            "sonarcloud"
        ),

        // 16. Tools & Utilities
        "Tools & Utilities" to listOf(
            // CLI
            "cli", "cli-tool", "cli-app", "command-line", "command-line-tool",
            "terminal", "terminal-ui", "terminal-app", "shell", "bash", "bash-script",
            "zsh", "fish", "powershell", "powershell-module",
            // Editors
            "vscode", "vscode-extension", "visual-studio-code", "visual-studio",
            "neovim", "nvim", "neovim-plugin", "neovim-config", "neovim-configuration",
            "neovim-dotfiles", "vim",
            "cursor", "cursor-ai", "cursor-ide", "cursor-ai-editor",
            "ide", "web-ide", "intellij-plugin", "intellij",
            // AI Coding Tools
            "windsurf-ai", "cursorai", "bolt", "cline",
            "github-copilot", "coding-assistant", "coding-agents", "ai-coding",
            "agentic-coding", "code-execution",
            // Tools
            "tools", "developer-tools", "development-tools", "devtools", "dev-tools",
            "utilities", "utility", "util", "utils", "helper",
            "productivity", "productivity-tools", "productivity-tool", "productivity-app",
            "toolkit", "manager", "downloader", "calculator", "calculator-application",
            "yt-dlp", "console",
            // Version Control
            "git", "github", "github-api", "github-pages", "github-profile",
            "github-readme", "github-readme-profile", "github-profile-readme",
            "github-stats", "github-page",
            "gitlab", "version-control", "git-scraping",
            // Package Managers
            "package-manager", "package-management", "npm", "pnpm", "yarn",
            "maven", "gradle", "gradle-plugin", "pip", "pypi", "cargo",
            "homebrew", "apt", "scoop", "scoop-bucket", "scoop-apps",
            "aur", "pkgbuild", "pkgbuilds",
            // Dotfiles
            "dotfiles", "dotfiles-linux", "dotfiles-macos", "chezmoi",
            "configuration", "config", "configuration-management", "metadata",
            "nixos-config", "nixos-configuration", "nix-darwin", "nix-flake", "nix-flakes",
            "flake",
            // Documentation
            "documentation", "docs", "readme", "readme-md",
            "markdown", "markdown-lang", "markdown-language", "markdown-editor",
            "mdx", "latex", "mkdocs", "docusaurus", "docusaurus2",
            // Others
            "addon", "plugin-manager", "extension", "plugin", "plugins",
            "screenshot", "screen-capture", "clipboard", "copy-paste",
            "anki", "anki-addon", "obsidian", "obsidian-plugin", "obsidian-md",
            "notion", "evernote", "onenote",
            "time-tracker", "time-management", "task-manager", "todo", "to-do-app",
            "todolist", "todolist-application", "tasks", "task-scheduling",
            "note", "note-taking", "notes", "notebook",
            "bookmark", "bookmarks", "bookmarklet", "read-later",
            "rss-reader", "feed-reader", "feeds",
            "calendar", "calendars", "scheduling", "appointment-booking",
            "email", "email-client", "mail", "imap", "smtp", "smtp-server",
            "launcher", "app-launcher", "spotlight", "alfred",
            "window-manager", "i3", "sway", "swaywm", "wlroots",
            "kde", "gnome", "xfce", "desktop-environment", "desktop-customization",
            "x11", "wayland", "display-server",
            "color-scheme", "color", "colors", "theme", "theming",
            "font", "font-awesome", "icons", "icon-pack",
            "widgets", "widget", "dashboard", "status-bar", "statusbar",
            "file-manager", "file-explorer", "file-transfer", "file-sharing",
            "sync", "synchronization", "backup", "restore",
            "archive", "archiver", "compression", "zip", "tar", "7zip",
            "pdf", "pdf-tools", "pdf-to-json", "docx", "document-processing",
            "converter", "conversion", "convert", "transform",
            "image-editing", "image-processing", "image-manipulation",
            "screenshot-tool", "screen-recorder", "video-editing",
            "downloader", "download-manager", "aria2",
            "media-player", "media-library", "music-player", "video-player",
            "music-library", "player",
            "translator", "translation", "i18n", "internationalization",
            "transcribe", "transcription", "captions", "subtitles",
            "ocr", "tesseract", "tesseract-ocr",
            "qr-code", "qr-generator", "barcode",
            "url-shortener", "link-shortener", "url",
            "scraper", "web-scraper", "spider", "crawler", "crawling",
            "automation-tools", "macro", "macros", "scripting",
            "utility-library", "helper-library", "common",
            "cli-framework", "cli-builder", "command-parser",
            "tui", "terminal-ui", "curses", "ncurses", "bubbletea", "ratatui",
            "ascii", "ascii-art", "unicode", "emoji",
            "random", "random-generator", "uuid", "guid",
            "hash", "hashing", "md5", "sha", "checksum",
            "diff", "diff-tool", "merge-tool", "patch",
            "linter", "formatter", "pretty-print", "beautifier",
            "installer", "setup", "install", "update", "upgrade",
            "cleaner", "cleanup", "garbage-collection",
            "monitor", "monitoring", "system-monitor", "resource-monitor",
            "logger", "log-viewer", "log-parser",
            "debug", "debugging", "debugging-tool", "debugger",
            "profiler", "profiling", "benchmark", "benchmarking",
            "test-runner", "test-harness", "test-framework",
            "mock", "mocking", "stub", "faker",
            "generator", "code-generator", "codegen", "boilerplate-generator",
            "scaffold", "scaffolding", "template-engine",
            "vim-plugin", "emacs", "emacs-lisp", "emacs-configuration",
            "tmux", "screen", "multiplexer",
            "zellij", "wezterm", "alacritty", "kitty",
            "personal-knowledge-management", "pkm", "zettelkasten",
            "flashcard", "flashcards", "spaced-repetition", "srs",
            "mnemonic", "memory-aid",
            "research-tools", "research-tool", "academic-research",
            "reference-manager", "bibliography", "citation",
            "diagram", "diagramming", "flowchart", "mind-map",
            "graphviz", "mermaid", "plantuml", "drawio",
            "whiteboard", "collaborative-whiteboard", "excalidraw",
            "presentation", "slides", "slideshow",
            "gif", "gif-maker", "gif-creator", "animation",
            "webp", "avif", "image-format",
            "user-guide", "guide", "walkthrough", "tutorial", "tutoriel",
            "cheatsheet", "cheat-sheet", "reference", "quick-reference",
            "badge", "shield", "status-badge", "build-badge"
        ),

        // 17. Finance & Trading
        "Finance & Trading" to listOf(
            "finance", "fintech", "financial", "financial-analysis",
            "trading", "trading-bot", "trading-strategies", "trading-algorithms",
            "trading-automation", "trading-platform", "trading-signal",
            "trading-signal-api", "trading-signal-bot", "trading-signals-info",
            "algorithmic-trading", "quantitative-finance", "quantitative-analysis",
            "quantitative-trading", "quant", "hft", "high-frequency-trading",
            "stock-market", "stocks", "equity", "stock-data", "stock-analysis",
            "stock-price-prediction", "price-prediction",
            "forecasting", "prediction", "backtesting",
            "investment", "investment-strategies", "portfolio-management",
            "portfolio", "asset-management", "wealth-management",
            "accounting", "budgeting", "budget", "money-tracker", "personal-finance",
            "cash-flow", "invoice", "invoicing", "billing",
            "payment", "payments", "payment-processing", "payment-gateway",
            "payment-integration", "transactions", "transaction",
            "stripe", "stripe-payments", "paypal", "square", "braintree",
            "credit-card", "card", "debit-card",
            "banking", "fintech-app", "neobank", "open-banking",
            "business-intelligence", "business-analytics", "economics",
            "bi", "powerbi", "powerbi-report", "tableau",
            "exchange", "forex", "fx", "currency", "crypto-exchange",
            "arbitrage", "trader", "eth-arbitrage",
            "earning", "income", "profit", "cash", "money", "gain",
            "revenue", "sales", "cost", "expense",
            "binance", "binance-api", "coinbase", "kraken", "ftx",
            "yahoo-finance", "alpha-vantage", "financial-data",
            "market", "market-data", "market-analysis",
            "risk", "risk-management", "risk-analysis",
            "aml", "kyc", "compliance", "regulatory", "audit",
            "tax", "taxation", "accounting-software",
            "insurance", "insure-tech",
            "pos", "point-of-sale", "retail", "ecommerce-analytics",
            "subscription", "recurring-billing", "saas-billing",
            "legal", "legal-tech", "contract",
            "operations-research", "optimization", "profit-maximization",
            "supply-chain", "logistics", "inventory", "procurement",
            "erp", "crm", "salesforce", "hubspot", "odoo"
        )
    )

    /**
     * Topics to exclude (too broad or meaningless)
     */
    val EXCLUDED_TOPICS = setOf(
        // Generic Words
        "open", "free", "simple", "basic", "general", "common",
        "learn", "learning", "tutorial", "guide", "example", "examples",
        "demo", "test", "experimental", "poc", "prototype", "proof-of-concept",
        "project", "template", "boilerplate", "starter", "starter-template", "starter-kit",
        "awesome", "collection", "list", "resources", "awesome-list", "awesome-lists",
        // Years
        "2025", "2024", "2023", "2026", "hacktoberfest", "hacktoberfest2025",
        "hacktoberfest2024", "hacktoberfest2023", "hacktoberfest2022",
        "hacktoberfest2021", "hacktoberfest2020", "hacktoberfest-accepted",
        // Community
        "opensource", "open-source", "community", "repository", "repositories",
        "good-first-issue", "course", "resume", "assistant",
        "peer-to-peer", "p2p", "client-side", "high-performance",
        "compliance", "healthcare", "games", "unix", "seed",
        "build", "vibe-coding", "vibecode",
        // Learning/Education
        "learning-resources", "educational-resources", "educational-project",
        "educational-tools", "students", "study", "curriculum",
        "internship", "internships", "course-project", "university-project",
        "assignment", "homework", "exam", "assessment",
        "udemy", "coursera", "edx", "mooc",
        "learning", "learn", "tutorial", "tutorials", "guide", "guides",
        "interactive-learning", "e-learning",
        // Other General
        "opensource", "foss", "open-access", "open-data", "open-science",
        "contributions", "contributions-welcome", "contribution",
        "help-wanted", "good-first-issue", "beginner-friendly", "for-beginners",
        "personal", "personal-project", "pet-project", "hobby-project",
        "portfolio", "portfolio-page", "portfolio-site", "portfolio-website",
        "personal-portfolio", "porfolio",
        "homepage", "website", "webpage",
        "skills", "samples", "code-samples", "code-examples",
        "reference", "specifications", "specification", "standard", "standards",
        "addon", "component", "plugin", "extension", "widget", "widgets",
        // Too Broad Topics
        "software", "software-tools", "software-library", "software-architecture",
        "software-testing", "software-library",
        "application", "applications", "development", "app",
        "technology", "tech", "digital", "online", "internet",
        "framework", "frameworks", "library", "libraries",
        "product", "platform", "service", "services",
        "tools", "tool", "utility", "utilities", "util", "utils",
        "manager", "management", "admin", "administration",
        "system", "systems", "system-tools",
        "data", "database", "databases", "db", "dbms",
        "api", "backend", "frontend", "fullstack",
        "web", "mobile", "desktop",
        "cloud", "serverless",
        "automation", "automatic",
        "integration", "enterprise-integration",
        // Documentation/README
        "readme", "readme-md", "documentation", "docs",
        "awesome-readme", "technical-documentation",
        "user-guide", "manual", "handbook",
        // Project Types
        "mvp", "poc", "proof-of-concept", "prototype", "prototyping",
        "demo", "demo-project", "demo-app",
        "example", "examples", "sample", "samples",
        "template", "templates", "boilerplate", "boilerplate-template",
        "starter", "starter-template", "starter-kit", "quickstart",
        "scaffold", "scaffolding", "skeleton",
        // Status/Quality
        "beta", "alpha", "experimental", "wip", "work-in-progress",
        "deprecated", "legacy", "archived", "archive",
        "stable", "production", "production-ready",
        "research", "research-project", "research-tool", "research-tools",
        // License/Legal
        "mit-license", "apache", "gpl", "bsd", "license",
        "legal", "terms-of-service", "terms-and-conditions", "privacy-policy",
        // Other Community
        "collaboration", "collaborate", "collaborative", "collaboration-tools",
        "work-from-home", "remote", "remote-development", "remote-work",
        "team", "teams", "teamwork", "pair-programming",
        "code-review", "pull-request", "pull-requests", "pr",
        "issue", "issues", "bug", "bugs", "bugfix",
        "commit", "commits", "contribution-graph",
        // Generic Adjectives
        "awesome", "amazing", "cool", "best", "top", "popular", "trending",
        "new", "latest", "modern", "advanced", "professional",
        "easy", "simple", "quick", "fast", "lightweight",
        "powerful", "complete", "full", "comprehensive",
        "free", "open", "public", "shared",
        "custom", "customizable", "configurable", "flexible",
        "scalable", "scalable-architecture", "extensible",
        "multi", "multi-platform", "cross-platform", "multiplatform",
        "universal", "generic", "common",
        // Actions/Status
        "install", "installer", "setup", "update", "upgrade",
        "deploy", "deployment", "build", "building",
        "test", "testing", "debug", "debugging",
        "monitor", "monitoring", "track", "tracking",
        // Others
        "fun", "game", "games", "gaming",
        "misc", "miscellaneous", "other", "others", "various",
        "collection", "collections", "list", "lists", "starred",
        "notes", "note", "blog", "blogs", "article", "articles",
        "book", "books", "ebook", "ebooks", "writing",
        "video", "videos", "tutorial-video",
        "podcast", "podcasts",
        "music", "song", "sound", "audio",
        "image", "images", "photo", "photos", "picture",
        "text", "document", "documents", "file", "files",
        // Region/Language
        "english", "english-language", "chinese", "japanese", "korean",
        "french", "german", "spanish", "portuguese", "russian",
        "vietnamese", "vietnamese-language", "arabic", "hindi",
        "language", "languages", "natural-language",
        // Industry/Domain (too generic)
        "business", "enterprise", "commerce", "retail",
        "healthcare", "medical", "health",
        "education", "educational",
        "finance", "financial",
        "science", "scientific", "science-research",
        "engineering", "it", "information-technology",
        // Specific Companies/Products (too generic)
        "github", "google", "microsoft", "amazon", "aws", "azure",
        "facebook", "twitter", "instagram", "youtube", "tiktok",
        // Other Exclusions
        "hactoberfest", "advent-of-code", "advent-of-code-2025",
        "ctf", "capture-the-flag",
        "conference", "conferences", "meetup", "meetups",
        "community-project", "open-terms-archive",
        "seanpm2001-", "seanpm2001-web", "seanpm2001-vexillology",
        "seanpm2001-games", "seanpm2001-lifearchive", "seanpm2001-flag-simulator",
        "flag-simulator", "flag-simulator-development",
        "softmicro-", "softmicro-documentation", "softmicro-drapes", "softmicro-project",
        "openad-", "openad-specification", "openad-development",
        "snu-2d", "snu-programming-tools"
    )
}