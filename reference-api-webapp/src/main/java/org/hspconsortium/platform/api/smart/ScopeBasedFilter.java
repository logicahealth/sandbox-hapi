/**
 *  * #%L
 *  *
 *  * %%
 *  * Copyright (C) 2014-2020 Healthcare Services Platform Consortium
 *  * %%
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *  * #L%
 */

///*
// * #%L
// * hspc-authorization-server
// * %%
// * Copyright (C) 2014 - 2015 Healthcare Services Platform Consortium
// * %%
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// * #L%
// */
//
//package org.hspconsortium.platform.api.smart;
//
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;
//import java.io.IOException;
//
//@Component("scopeFilter")
//public class ScopeBasedFilter extends OncePerRequestFilter {
//
//    // todo this should be in a common artifact instead
//    String LAUNCH_CONTEXT_ID_KEY = "LAUNCH_CONTEXT_ID";
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        HttpSession session = request.getSession();
//        String launchContextId = (String)session.getAttribute(LAUNCH_CONTEXT_ID_KEY);
////            Set<String> launchContextIds = (Set<String>)session.getAttribute(LAUNCH_CONTEXT_ID_MAP_KEY);
////        try {
////            Set<String> launchContextIds = (Set<String>)session.getAttribute(Constants.LAUNCH_CONTEXT_ID_MAP_KEY);
////            LaunchContextHolder.setLaunchContextIds(launchContextIds);
////
////            filterChain.doFilter(request, response);
////
////        } finally {
////            session.setAttribute(Constants.LAUNCH_CONTEXT_ID_MAP_KEY, LaunchContextHolder.getLaunchContextIds());
////            LaunchContextHolder.clearLaunchContextIds();
////        }
//    }
//}
