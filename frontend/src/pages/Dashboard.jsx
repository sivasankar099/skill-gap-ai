import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { apiService } from '../services/api';
import useAuth from '../hooks/useAuth';
import {
  LayoutDashboard,
  LogOut,
  Upload,
  BookOpen,
  Video,
  Award,
  FileText,
  AlertCircle,
  HelpCircle,
  ExternalLink,
  ChevronDown,
  ChevronUp,
  Briefcase,
  Calendar,
  Compass
} from 'lucide-react';

export const Dashboard = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [analysis, setAnalysis] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [openWeek, setOpenWeek] = useState(1);

  useEffect(() => {
    const fetchLatestAnalysis = async () => {
      try {
        const data = await apiService.getLatestAnalysis();
        setAnalysis(data);
      } catch (err) {
        if (err.response && err.response.status === 404) {
          // No analysis found is expected for new users, we will show a starter screen
          setAnalysis(null);
        } else {
          setError('Failed to connect to the backend server. Please verify database connectivity.');
        }
      } finally {
        setLoading(false);
      }
    };
    fetchLatestAnalysis();
  }, []);

  const toggleWeek = (weekNum) => {
    setOpenWeek(openWeek === weekNum ? null : weekNum);
  };

  const getResourceIcon = (type) => {
    switch (type?.toUpperCase()) {
      case 'VIDEO':
        return <Video size={16} className="text-rose-400" />;
      case 'PRACTICE':
        return <Award size={16} className="text-amber-400" />;
      case 'OFFICIAL':
      case 'DOCUMENTATION':
      default:
        return <BookOpen size={16} className="text-cyan-400" />;
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-slate-950 flex flex-col items-center justify-center text-slate-100">
        <div className="w-12 h-12 border-4 border-emerald-500/20 border-t-emerald-500 rounded-full animate-spin"></div>
        <p className="mt-4 text-slate-400 text-sm tracking-wide">Loading your dashboard...</p>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 flex flex-col">
      {/* Premium Navbar */}
      <nav className="glass border-b border-slate-900 px-6 py-4 flex items-center justify-between sticky top-0 z-40">
        <div className="flex items-center gap-2">
          <div className="w-9 h-9 rounded-xl bg-gradient-to-tr from-emerald-500 to-teal-500 flex items-center justify-center text-slate-950 font-bold text-lg">
            S
          </div>
          <span className="font-extrabold tracking-wide text-lg gradient-text">
            SkillGap AI
          </span>
        </div>
        
        <div className="flex items-center gap-6">
          <div className="hidden sm:flex items-center gap-2">
            <div className="w-2.5 h-2.5 rounded-full bg-emerald-500 animate-ping"></div>
            <span className="text-xs text-slate-400 font-semibold tracking-wider uppercase">
              User: <span className="text-slate-200">{user}</span>
            </span>
          </div>
          
          <button
            onClick={() => { logout(); navigate('/login'); }}
            className="flex items-center gap-2 text-slate-400 hover:text-rose-400 text-sm font-medium transition-all cursor-pointer"
          >
            <LogOut size={16} />
            <span className="hidden sm:inline">Logout</span>
          </button>
        </div>
      </nav>

      {/* Main Dashboard Layout */}
      <main className="flex-1 max-w-7xl w-full mx-auto p-6 space-y-8">
        
        {error && (
          <div className="flex items-center gap-3 text-rose-400 bg-rose-500/10 border border-rose-500/20 p-4 rounded-xl text-sm">
            <AlertCircle size={20} className="shrink-0" />
            <span>{error}</span>
          </div>
        )}

        {!analysis ? (
          /* Landing Starter View */
          <div className="flex flex-col items-center justify-center py-20 text-center space-y-6 animate-fadeIn">
            <div className="w-20 h-20 rounded-2xl bg-emerald-500/10 border border-emerald-500/20 flex items-center justify-center text-emerald-400">
              <Compass size={40} className="animate-pulse-slow" />
            </div>
            <div className="space-y-2 max-w-md">
              <h2 className="text-2xl font-extrabold text-white">No Skills Profile Found</h2>
              <p className="text-slate-400 text-sm leading-relaxed">
                Analyze your resume, identify missing competencies, and chart your custom learning roadmap in seconds.
              </p>
            </div>
            <button
              onClick={() => navigate('/upload')}
              className="px-6 py-3 bg-gradient-to-r from-emerald-500 to-teal-500 hover:from-emerald-400 hover:to-teal-400 text-slate-950 font-bold rounded-lg shadow-lg hover:shadow-emerald-500/20 active:scale-[0.98] transition-all duration-200 text-sm flex items-center gap-2 cursor-pointer"
            >
              <Upload size={18} />
              Upload Resume PDF
            </button>
          </div>
        ) : (
          /* Main Dashboard Results */
          <div className="space-y-8 animate-fadeIn">
            
            {/* Upper Info Grid */}
            <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
              
              {/* Score card & Skills Panel */}
              <div className="lg:col-span-1 flex flex-col gap-6">
                
                {/* Visual Circle Score Ring */}
                <div className="glass rounded-2xl p-6 flex flex-col items-center justify-center relative overflow-hidden">
                  <div className="absolute top-0 right-0 p-3 text-emerald-500/20">
                    <LayoutDashboard size={40} />
                  </div>
                  <h3 className="text-slate-400 text-xs font-semibold uppercase tracking-wider mb-4 block self-start">
                    Skill Score
                  </h3>
                  
                  {/* SVG Circle Progress */}
                  <div className="relative flex items-center justify-center w-40 h-40">
                    <svg className="w-full h-full transform -rotate-90" viewBox="0 0 100 100">
                      <circle
                        cx="50"
                        cy="50"
                        r="42"
                        className="stroke-slate-800"
                        strokeWidth="8"
                        fill="transparent"
                      />
                      <circle
                        cx="50"
                        cy="50"
                        r="42"
                        className="stroke-emerald-500 transition-all duration-1000 ease-out"
                        strokeWidth="8"
                        fill="transparent"
                        strokeDasharray={263.89}
                        strokeDashoffset={263.89 - (263.89 * analysis.skillScore) / 100}
                        strokeLinecap="round"
                      />
                    </svg>
                    <div className="absolute flex flex-col items-center justify-center">
                      <span className="text-4xl font-extrabold text-white">
                        {Math.round(analysis.skillScore)}%
                      </span>
                      <span className="text-[10px] text-slate-500 font-semibold tracking-wider uppercase mt-1">
                        Match Score
                      </span>
                    </div>
                  </div>

                  <div className="mt-4 flex items-center gap-1.5 text-slate-400 text-xs">
                    <Briefcase size={14} />
                    <span>Target: <span className="font-semibold text-slate-200">{analysis.roleName}</span></span>
                  </div>
                </div>

                {/* Match Lists */}
                <div className="glass rounded-2xl p-6 space-y-6 flex-1">
                  
                  {/* Strong Skills */}
                  <div className="space-y-3">
                    <h4 className="text-emerald-400 font-semibold text-sm border-b border-slate-900 pb-2 flex items-center justify-between">
                      <span>Strong Skills</span>
                      <span className="text-xs bg-emerald-500/10 px-2 py-0.5 rounded text-emerald-400 font-medium">
                        {analysis.strongSkills?.length || 0}
                      </span>
                    </h4>
                    <div className="flex flex-wrap gap-2">
                      {analysis.strongSkills?.length > 0 ? (
                        analysis.strongSkills.map((skill, idx) => (
                          <span key={idx} className="bg-emerald-500/5 text-emerald-400 text-xs px-2.5 py-1 rounded-md border border-emerald-500/10">
                            {skill}
                          </span>
                        ))
                      ) : (
                        <p className="text-slate-500 text-xs italic">No matching skills detected</p>
                      )}
                    </div>
                  </div>

                  {/* Missing Skills */}
                  <div className="space-y-3">
                    <h4 className="text-rose-400 font-semibold text-sm border-b border-slate-900 pb-2 flex items-center justify-between">
                      <span>Missing Skills</span>
                      <span className="text-xs bg-rose-500/10 px-2 py-0.5 rounded text-rose-400 font-medium">
                        {analysis.missingSkills?.length || 0}
                      </span>
                    </h4>
                    <div className="flex flex-wrap gap-2">
                      {analysis.missingSkills?.length > 0 ? (
                        analysis.missingSkills.map((skill, idx) => (
                          <span key={idx} className="bg-rose-500/5 text-rose-400 text-xs px-2.5 py-1 rounded-md border border-rose-500/10">
                            {skill}
                          </span>
                        ))
                      ) : (
                        <p className="text-slate-400 text-xs italic text-emerald-400 font-semibold">You have all required skills!</p>
                      )}
                    </div>
                  </div>
                </div>

              </div>

              {/* Career Roadmap Accordion Panel */}
              <div className="lg:col-span-2 glass rounded-2xl p-6 flex flex-col">
                <div className="flex items-center justify-between border-b border-slate-900 pb-4 mb-4">
                  <div>
                    <h3 className="text-lg font-bold text-white flex items-center gap-2">
                      <Calendar size={18} className="text-emerald-400" />
                      4-Week Career Action Plan
                    </h3>
                    <p className="text-slate-400 text-xs mt-0.5">
                      Tailored by SkillGap AI to get you job-ready.
                    </p>
                  </div>
                  
                  <button
                    onClick={() => navigate('/upload')}
                    className="px-3.5 py-1.5 bg-slate-900 border border-slate-800 hover:border-slate-700 rounded-lg text-xs font-semibold text-slate-300 hover:text-white transition-all flex items-center gap-1.5 cursor-pointer"
                  >
                    <Upload size={14} />
                    New Scan
                  </button>
                </div>

                {/* Gap Explanation */}
                <div className="bg-slate-900/40 border border-slate-900 p-4 rounded-xl mb-6 text-sm text-slate-300 leading-relaxed italic">
                  "{analysis.gapExplanation}"
                </div>

                {/* Weeks Listing */}
                <div className="space-y-3 flex-1 overflow-y-auto max-h-[380px] pr-2">
                  {analysis.roadmap?.weeks?.map((week) => {
                    const isOpen = openWeek === week.weekNumber;
                    return (
                      <div
                        key={week.weekNumber}
                        className={`border rounded-xl transition-all duration-300 overflow-hidden ${
                          isOpen ? 'border-emerald-500/30 bg-emerald-500/[0.01]' : 'border-slate-900 bg-slate-900/20'
                        }`}
                      >
                        {/* Header */}
                        <div
                          onClick={() => toggleWeek(week.weekNumber)}
                          className="px-5 py-4 flex items-center justify-between cursor-pointer hover:bg-slate-900/30 transition-all select-none"
                        >
                          <div className="flex items-center gap-3">
                            <span className={`w-8 h-8 rounded-lg flex items-center justify-center text-xs font-bold ${
                              isOpen ? 'bg-emerald-500/20 text-emerald-400' : 'bg-slate-800 text-slate-400'
                            }`}>
                              W{week.weekNumber}
                            </span>
                            <span className="font-bold text-sm text-white">
                              {week.focus}
                            </span>
                          </div>
                          {isOpen ? <ChevronUp size={16} className="text-slate-400" /> : <ChevronDown size={16} className="text-slate-400" />}
                        </div>

                        {/* Collapsible Content */}
                        {isOpen && (
                          <div className="px-5 pb-4 pt-1 space-y-3 border-t border-slate-900 animate-slideDown">
                            <div className="space-y-1">
                              <h5 className="text-slate-400 text-[10px] font-bold tracking-wider uppercase">Topics to learn</h5>
                              <div className="flex flex-wrap gap-1.5 pt-1">
                                {week.topics?.map((topic, idx) => (
                                  <span key={idx} className="bg-slate-900 text-slate-300 text-xs px-2.5 py-0.5 rounded-full border border-slate-800">
                                    {topic}
                                  </span>
                                ))}
                              </div>
                            </div>
                            
                            <div className="space-y-0.5">
                              <h5 className="text-slate-400 text-[10px] font-bold tracking-wider uppercase">Weekly Milestone</h5>
                              <p className="text-slate-300 text-xs leading-relaxed">{week.outcome}</p>
                            </div>
                          </div>
                        )}
                      </div>
                    );
                  })}
                </div>
              </div>

            </div>

            {/* Qdrant RAG Recommended Learning Resources Catalog */}
            <div className="glass rounded-2xl p-6 space-y-6">
              <div>
                <h3 className="text-lg font-bold text-white flex items-center gap-2">
                  <Compass size={18} className="text-emerald-400" />
                  Semantic Search Recommendations (RAG)
                </h3>
                <p className="text-slate-400 text-xs mt-0.5">
                  Semantic matches retrieved from the Qdrant database vector index for your missing competencies.
                </p>
              </div>

              {analysis.learningResources?.length > 0 ? (
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                  {analysis.learningResources.map((res) => (
                    <div
                      key={res.id}
                      className="bg-slate-900/40 hover:bg-slate-900/60 border border-slate-900 rounded-xl p-5 hover:border-slate-800 transition-all duration-300 flex flex-col justify-between group"
                    >
                      <div className="space-y-3">
                        <div className="flex items-center justify-between">
                          <span className="text-[10px] bg-slate-800/80 border border-slate-700/50 px-2 py-0.5 rounded text-slate-400 font-semibold uppercase tracking-wide">
                            {res.resourceType}
                          </span>
                          <span className="text-[10px] bg-emerald-500/10 px-2 py-0.5 rounded text-emerald-400 font-medium">
                            Mapped: {res.skillName}
                          </span>
                        </div>
                        
                        <div className="space-y-1">
                          <h4 className="text-sm font-bold text-white group-hover:text-emerald-400 transition-all">
                            {res.title}
                          </h4>
                          <p className="text-xs text-slate-400 leading-relaxed line-clamp-2">
                            {res.description}
                          </p>
                        </div>
                      </div>

                      <div className="mt-4 pt-3 border-t border-slate-900 flex items-center justify-between text-xs font-semibold">
                        <span className="flex items-center gap-1.5 text-slate-500">
                          {getResourceIcon(res.resourceType)}
                          <span className="uppercase text-[10px] tracking-wide">{res.resourceType}</span>
                        </span>
                        
                        <a
                          href={res.url}
                          target="_blank"
                          rel="noopener noreferrer"
                          className="flex items-center gap-1 text-emerald-400 hover:text-emerald-300 transition-all cursor-pointer"
                        >
                          Access
                          <ExternalLink size={12} />
                        </a>
                      </div>
                    </div>
                  ))}
                </div>
              ) : (
                <div className="bg-slate-900/20 border border-slate-900 rounded-xl p-8 flex flex-col items-center justify-center text-center">
                  <BookOpen size={24} className="text-slate-600 mb-2" />
                  <p className="text-slate-400 text-xs italic">
                    All required skills are present! No missing skill recommendation matching needed.
                  </p>
                </div>
              )}
            </div>

          </div>
        )}
      </main>

      {/* Footer */}
      <footer className="py-6 text-center border-t border-slate-900 text-xs text-slate-500">
        SkillGap AI © {new Date().getFullYear()} — Built as a Production-Grade Final Year Project.
      </footer>
    </div>
  );
};

export default Dashboard;
